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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class QuizAttemptServiceImpl implements QuizAttemptService {
  public static final String LOGGED_IN_USER = "LOGGED_IN_USER";
  private final QuizRepository quizRepository;
  private final QuizAttemptRepository quizAttemptRepository;
  private final AuditorAware<String> principal;

  QuizAttemptServiceImpl(
      final QuizRepository quizRepository,
      final QuizAttemptRepository quizAttemptRepository,
      final AuditorAware<String> principal) {
    this.quizRepository = quizRepository;
    this.quizAttemptRepository = quizAttemptRepository;
    this.principal = principal;
  }

  @Override
  public QuizDto createQuizAttempt(final UUID quizId, final QuizDto request) {
    log.info("Attempting quiz {}", quizId);
    if (!quizId.equals(request.getId())) {
      throw new BadRequestException("Inconsistent quiz id provided in the payload");
    }
    final Quiz quiz = quizRepository.findById(quizId).orElseThrow(
        () -> new NotFoundException(Quiz.class.getCanonicalName(), quizId));
    final String currentUser =
        principal.getCurrentAuditor()
            .orElseThrow(() -> new NotFoundException(LOGGED_IN_USER, null));
    if (quiz.getCreatedBy().equals(currentUser)) {
      throw new BadRequestException("user can not take his own quiz");
    }
    if (!quiz.isPublished()) {
      throw new BadRequestException("Quiz has not published yet");
    }
    if (quizAttemptRepository.existsByQuizAndCreatedBy(quiz, currentUser)) {
      throw new BadRequestException("user has already attempted the quiz");
    }
    final QuizAttempt quizAttempt = new QuizAttempt();
    quizAttempt.setQuiz(quiz);
    calculateQuestionScore(quizAttempt, request);
    final double quizScore =
        quizAttempt.getQuestionAttempts().stream().map(QuestionAttempt::getScore)
            .reduce(0.0, Double::sum);
    quizAttempt.setScore(quizScore);
    quizAttemptRepository.save(quizAttempt);
    return buildQuizAttemptDto(quizAttempt);
  }


  private void calculateQuestionScore(final QuizAttempt quizAttempt, final QuizDto request) {
    quizAttempt.getQuiz().getQuestions().forEach(question -> {
      final QuestionAttempt questionAttempt = new QuestionAttempt();
      questionAttempt.setQuestion(question);
      final QuestionDto questionReply =
          request.getQuestions().stream().filter(q -> question.getId().equals(q.getId()))
              .findAny().orElse(null);
      log.debug("Question {} skipped", question.getText());
      // skipped
      if (questionReply == null) {
        questionAttempt.setSkipped(true);
        questionAttempt.setSelectedOptionIds("");
        questionAttempt.setScore(0.0);
      } else {
        String selectedIds = questionReply.getOptions().stream()
            .map(selectedOption -> selectedOption.getId().toString())
            .collect(Collectors.joining(","));
        log.debug("Selected answers = {}", selectedIds);
        questionAttempt.setSkipped(false);
        questionAttempt.setSelectedOptionIds(selectedIds);
        questionAttempt.setScore(calculateQuestionScore(question, questionReply));
      }
      quizAttempt.addQuestionAttempt(questionAttempt);
    });
  }


  private double calculateQuestionScore(
      final Question question,
      final QuestionDto questionAttemptRequest) {
    final boolean isMultipleAnswer =
        question.getOptions().stream().filter(Option::isCorrect).count() > 1;
    log.debug("Calculating score for question = {}, isMultiple = {}", question.getText(),
        isMultipleAnswer);
    if (isMultipleAnswer) {
      return calculateMultipleAnswerQuestionScore(question.getOptions(),
          questionAttemptRequest.getOptions());
    }
    return calculateSingleAnswerQuestionScore(question.getOptions(),
        questionAttemptRequest.getOptions());
  }

  private double calculateMultipleAnswerQuestionScore(final List<Option> options,
                                                      final List<OptionDto> selectedOptions) {
    if (selectedOptions.isEmpty()) {
      return 0;
    }
    final double correct = options.stream().filter(Option::isCorrect).count();
    final double incorrect = options.size() - correct;
    double score = 0;
    for (final Option option : options) {
      final boolean isSelected = selectedOptions.stream().filter(selectedOption ->
          selectedOption.getId().equals(option.getId())).count() == 1;
      if (isSelected) {
        if (option.isCorrect()) {
          score += 1 / correct;
        } else {
          score -= 1 / incorrect; // if incorrect is 0 , is correct is always true.
        }
      }
    }
    log.debug("Score = {}", score);
    return score;
  }

  private double calculateSingleAnswerQuestionScore(
      final List<Option> options,
      final List<OptionDto> selectedOptions) {
    if (selectedOptions.isEmpty()) {
      return 0;
    }
    if (selectedOptions.size() > 1) {
      throw new BadRequestException("Expected at most one answer for single answer question");
    }
    final Option correctOption = options.stream().filter(Option::isCorrect).findAny()
        .orElseThrow(RuntimeException::new);
    final OptionDto selectedOption = selectedOptions.get(0);
    if (correctOption.getId().equals(selectedOption.getId())) {
      return 1;
    }
    return -1;
  }

  @Override
  public QuizPage getAttempts(final int pageNo, final int limit) {
    log.info("Fetch attempts, pageNo = {},limit = {}", pageNo, limit);
    final PageRequest pageRequest =
        PageRequest.of(pageNo, limit, Sort.by("createdAt").descending());
    final String currentUser =
        principal.getCurrentAuditor()
            .orElseThrow(() -> new NotFoundException(LOGGED_IN_USER, null));
    final Page<QuizDto> currentPage =
        quizAttemptRepository.findByCreatedBy(currentUser, pageRequest)
            .map(this::buildQuizAttemptDto);
    return new QuizPage(currentPage.getContent(), pageNo, currentPage.getTotalPages(), limit);
  }

  @Override
  public QuizPage getQuizStat(final UUID id, final int pageNo, final int limit) {
    log.info("Getting quiz stat for id = {} , page = {}, limit = {}",
        id, pageNo, limit);
    final Quiz quiz = quizRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(Quiz.class.getCanonicalName(), id));
    final String currentUser =
        principal.getCurrentAuditor()
            .orElseThrow(() -> new NotFoundException(LOGGED_IN_USER, null));
    if (!quiz.getCreatedBy().equals(currentUser)) {
      throw new BadRequestException("user can not view stat created by others");
    }
    final PageRequest pageRequest =
        PageRequest.of(pageNo, limit, Sort.by("createdAt").descending());
    final Page<QuizDto> currentPage = quizAttemptRepository.findByQuiz(quiz, pageRequest)
        .map(this::buildQuizAttemptDto);
    return new QuizPage(currentPage.getContent(), pageNo, currentPage.getTotalPages(), limit);
  }

  private QuizDto buildQuizAttemptDto(final QuizAttempt quizAttempt) {
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

  private List<QuestionDto> buildQuestionAttemptsDto(
      final List<QuestionAttempt> questionsAttempts) {
    return questionsAttempts.stream().map(questionAttempt -> {
      final Question question = questionAttempt.getQuestion();
      return QuestionDto.builder()
          .id(question.getId())
          .text(question.getText())
          .skipped(questionAttempt.isSkipped())
          .score(questionAttempt.getScore())
          .options(buildOptionsDto(question.getOptions(), questionAttempt.getSelectedOptionIds()))
          .build();
    }).toList();
  }

  private List<OptionDto> buildOptionsDto(final List<Option> options,
                                          final String selectedOptionIds) {
    final Map<String, Boolean> selected = Arrays.stream(selectedOptionIds.split(",")).collect(
        Collectors.toMap(s -> s, s -> true));
    return options.stream().map(
        option -> OptionDto.builder()
            .text(option.getText())
            .id(option.getId())
            .selected(selected.getOrDefault(option.getId().toString(), false))
            .build()).toList();

  }
}
