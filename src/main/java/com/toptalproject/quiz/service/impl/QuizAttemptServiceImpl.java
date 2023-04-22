package com.toptalproject.quiz.service.impl;

import com.toptalproject.quiz.data.entity.Option;
import com.toptalproject.quiz.data.entity.Question;
import com.toptalproject.quiz.data.entity.QuestionAttempt;
import com.toptalproject.quiz.data.entity.Quiz;
import com.toptalproject.quiz.data.entity.QuizAttempt;
import com.toptalproject.quiz.data.repository.QuizAttemptRepository;
import com.toptalproject.quiz.data.repository.QuizRepository;
import com.toptalproject.quiz.dto.OptionDto;
import com.toptalproject.quiz.dto.QuestionDto;
import com.toptalproject.quiz.dto.QuizDto;
import com.toptalproject.quiz.dto.QuizPage;
import com.toptalproject.quiz.error.BadRequestException;
import com.toptalproject.quiz.error.NotFoundException;
import com.toptalproject.quiz.service.QuizAttemptService;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
class QuizAttemptServiceImpl implements QuizAttemptService {
  private final QuizRepository quizRepository;
  private final QuizAttemptRepository quizAttemptRepository;
  private final AuditorAware<String> principal;

  QuizAttemptServiceImpl(QuizRepository quizRepository, QuizAttemptRepository quizAttemptRepository,
                         AuditorAware<String> principal) {
    this.quizRepository = quizRepository;
    this.quizAttemptRepository = quizAttemptRepository;
    this.principal = principal;
  }

  @Override
  public QuizDto createQuizAttempt(QuizDto request) {
    Quiz quiz = quizRepository.findById(request.getId()).orElseThrow(
        () -> new NotFoundException(Quiz.class.getCanonicalName(), request.getId()));
    if (quiz.getCreatedBy().equals(principal.getCurrentAuditor().get())) {
      throw new BadRequestException("user can not take his own quiz");
    }
    if (!quiz.isPublished()) {
      throw new BadRequestException("Quiz has not published yet");
    }
    if (quizAttemptRepository.existsByQuizAndCreatedBy(quiz, principal.getCurrentAuditor().get())) {
      throw new BadRequestException("user has already attempted the quiz");
    }
    QuizAttempt quizAttempt = new QuizAttempt();
    quizAttempt.setQuiz(quiz);
    calculateQuestionScore(quizAttempt, request);
    double quizScore = quizAttempt.getQuestionAttempts().stream().map(QuestionAttempt::getScore)
        .reduce(0.0, Double::sum);
    quizAttempt.setScore(quizScore);
    quizAttemptRepository.save(quizAttempt);
    return buildQuizAttemptDto(quizAttempt);
  }


  private void calculateQuestionScore(QuizAttempt quizAttempt, QuizDto request) {
    quizAttempt.getQuiz().getQuestions().forEach(question -> {
      QuestionAttempt questionAttempt = new QuestionAttempt();
      questionAttempt.setQuestion(question);
      QuestionDto questionReply =
          request.getQuestions().stream().filter(q -> question.getId().equals(q.getId()))
              .findAny().orElse(null);

      // skipped
      if (questionReply == null) {
        questionAttempt.setSkipped(true);
        questionAttempt.setSelectedOptionIds("");
        questionAttempt.setScore(0.0);
      } else {
        if (!question.isMultipleAnswer() && questionReply.getOptions().size() > 1) {
          throw new BadRequestException("Multiple answer not allowed in a single answer question");
        }
        String selectedIds = questionReply.getOptions().stream()
            .map(selectedOption -> selectedOption.getId().toString())
            .collect(Collectors.joining(","));
        questionAttempt.setSkipped(false);
        questionAttempt.setSelectedOptionIds(selectedIds);
        questionAttempt.setScore(calculateQuestionScore(question, questionReply));
      }
      quizAttempt.addQuestionAttempt(questionAttempt);
    });
  }


  private double calculateQuestionScore(Question question,
                                        QuestionDto questionAttemptRequest) {
    if (question.isMultipleAnswer()) {
      return calculateMultipleAnswerQuestionScore(question.getOptions(),
          questionAttemptRequest.getOptions());
    }
    return calculateSingleAnswerQuestionScore(question.getOptions(),
        questionAttemptRequest.getOptions());
  }

  private double calculateMultipleAnswerQuestionScore(List<Option> options,
                                                      List<OptionDto> selectedOptions) {
    if (selectedOptions.isEmpty()) {
      return 0;
    }
    if (selectedOptions.size() == 1) {
      throw new BadRequestException("Expected more than one answer for multiple answer question");
    }
    double correct = options.stream().filter(Option::isCorrect).count();
    double incorrect = options.size() - correct;
    double score = 0;
    for (Option option : options) {
      boolean isSelected = selectedOptions.stream().filter(selectedOption ->
          selectedOption.getId().equals(option.getId())).count() == 1;
      if (isSelected) {
        if (option.isCorrect()) {
          score += 1 / correct;
        } else {
          score -= 1 / incorrect; // if incorrect is 0 , is correct is always true.
        }
      }
    }
    return score;
  }

  private double calculateSingleAnswerQuestionScore(List<Option> options,
                                                    List<OptionDto> selectedOptions) {
    if (selectedOptions.isEmpty()) {
      return 0;
    }
    if (selectedOptions.size() > 1) {
      throw new BadRequestException("Expected at most one answer for single answer question");
    }
    Option correctOption = options.stream().filter(Option::isCorrect).findAny()
        .orElseThrow(RuntimeException::new);
    OptionDto selectedOption = selectedOptions.get(0);
    if (correctOption.getId().equals(selectedOption.getId())) {
      return 1;
    }
    return -1;
  }

  @Override
  public QuizPage getAttempts(int pageNo, int limit) {
    PageRequest pageRequest = PageRequest.of(pageNo, limit, Sort.by("createdAt").descending());
    Page<QuizDto> currentPage =
        quizAttemptRepository.findByCreatedBy(principal.getCurrentAuditor().get(), pageRequest)
            .map(this::buildQuizAttemptDto);
    return new QuizPage(currentPage.getContent(), pageNo, currentPage.getTotalPages(), limit);
  }

  @Override
  public QuizPage getQuizStat(UUID id, int pageNo, Integer limit) {
    Quiz quiz = quizRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(Quiz.class.getCanonicalName(), id));
    if (!quiz.getCreatedBy().equals(principal.getCurrentAuditor().get())) {
      throw new BadRequestException("user can not view stat created by others");
    }
    PageRequest pageRequest = PageRequest.of(pageNo, limit, Sort.by("createdAt").descending());
    Page<QuizDto> currentPage = quizAttemptRepository.findByQuiz(quiz, pageRequest)
        .map(this::buildQuizAttemptDto);
    return new QuizPage(currentPage.getContent(), pageNo, currentPage.getTotalPages(), limit);
  }

  private QuizDto buildQuizAttemptDto(QuizAttempt quizAttempt) {
    return QuizDto.builder()
        .id(quizAttempt.getQuiz().getId())
        .published(quizAttempt.getQuiz().isPublished())
        .publishedAt(quizAttempt.getQuiz().getPublishedAt())
        .title(quizAttempt.getQuiz().getTitle())
        .score(quizAttempt.getScore())
        .attemptedBy(quizAttempt.getCreatedBy())
        .questions(buildQuestionAttemptsDto(quizAttempt.getQuestionAttempts()))
        .build();
  }

  private List<QuestionDto> buildQuestionAttemptsDto(List<QuestionAttempt> questionsAttempts) {
    return questionsAttempts.stream().map(questionAttempt -> {
      Question question = questionAttempt.getQuestion();
      return QuestionDto.builder()
          .id(question.getId())
          .text(question.getText())
          .skipped(questionAttempt.isSkipped())
          .score(questionAttempt.getScore())
          .options(buildOptionsDto(question.getOptions(), questionAttempt.getSelectedOptionIds()))
          .multipleAnswer(question.isMultipleAnswer()).build();
    }).toList();
  }

  private List<OptionDto> buildOptionsDto(List<Option> options, String selectedOptionIds) {
    Map<String, Boolean> selected = Arrays.stream(selectedOptionIds.split(",")).collect(
        Collectors.toMap(s -> s, s -> true));
    return options.stream().map(
        option -> OptionDto.builder()
            .text(option.getText())
            .id(option.getId())
            .selected(selected.getOrDefault(option.getId().toString(), false))
            .build()).toList();

  }
}
