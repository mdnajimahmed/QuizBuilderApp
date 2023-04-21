package com.toptalproject.quiz.service.impl;

import com.toptalproject.quiz.data.entity.Answer;
import com.toptalproject.quiz.data.entity.Question;
import com.toptalproject.quiz.data.entity.QuestionAttempt;
import com.toptalproject.quiz.data.entity.Quiz;
import com.toptalproject.quiz.data.entity.QuizAttempt;
import com.toptalproject.quiz.data.repository.QuizAttemptRepository;
import com.toptalproject.quiz.data.repository.QuizRepository;
import com.toptalproject.quiz.dto.AnswerDto;
import com.toptalproject.quiz.dto.QuestionDto;
import com.toptalproject.quiz.dto.QuizDto;
import com.toptalproject.quiz.error.BadRequestException;
import com.toptalproject.quiz.error.NotFoundException;
import com.toptalproject.quiz.service.QuizAttemptService;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  @Transactional
  public void createQuizAttempt(QuizDto request) {
    Quiz quiz = quizRepository.findById(request.getId()).orElseThrow(
        () -> new NotFoundException(Quiz.class.getCanonicalName(), request.getId()));
    if(quizAttemptRepository.existsByQuizAndCreatedBy(quiz,principal.getCurrentAuditor().get())){
      throw new BadRequestException("user has already attempted the quiz");
    }
    QuizAttempt quizAttempt = new QuizAttempt();
    quizAttempt.setQuiz(quiz);
    updateQuestionAttempt(quizAttempt, request);
    double quizScore = quizAttempt.getQuestionAttempts().stream().map(QuestionAttempt::getScore)
        .reduce(0.0, Double::sum);
    quizAttempt.setScore(quizScore);
    quizAttemptRepository.save(quizAttempt);
  }



  private void updateQuestionAttempt(QuizAttempt quizAttempt, QuizDto request) {
    request.getQuestions().forEach(questionAttemptRequest -> {
      Question question = quizAttempt.getQuiz().getQuestions().stream()
          .filter(q -> q.getId().equals(questionAttemptRequest.getId())).findAny()
          .orElseThrow(() -> new NotFoundException(
              Question.class.getCanonicalName(), questionAttemptRequest.getId()
          ));
      String selectedIds = questionAttemptRequest.getAnswers().stream()
          .map(answerAttemptRequest -> answerAttemptRequest.getId().toString())
          .collect(Collectors.joining(","));
      QuestionAttempt questionAttempt = new QuestionAttempt();
      questionAttempt.setSkipped(questionAttemptRequest.getAnswers().isEmpty());
      questionAttempt.setQuestion(question);
      questionAttempt.setScore(
          calculateQuestionScore(question, questionAttemptRequest));
      questionAttempt.setSelectedAnswerIds(selectedIds);
      quizAttempt.addQuestionAttempt(questionAttempt);
    });
  }


  private double calculateQuestionScore(Question question,
                                        QuestionDto questionAttemptRequest) {
    if (question.isMultipleAnswer()) {
      return calculateMultipleAnswerQuestionScore(question.getAnswers(),
          questionAttemptRequest.getAnswers());
    }
    return calculateSingleAnswerQuestionScore(question.getAnswers(),
        questionAttemptRequest.getAnswers());
  }

  private double calculateMultipleAnswerQuestionScore(List<Answer> answers,
                                                      List<AnswerDto> answerAttempts) {
    if (answerAttempts.size() == 0) {
      return 0;
    }
    if (answerAttempts.size() == 1) {
      throw new BadRequestException("Expected more than one answer for multiple answer question");
    }
    double correct = answers.stream().filter(Answer::isCorrect).count();
    double incorrect = answers.size() - correct;
    double score = 0;
    for (Answer answer : answers) {
      boolean isSelected = answerAttempts.stream().filter(answerAttemptRequest ->
          answerAttemptRequest.getId().equals(answer.getId())).count() == 1;
      if (isSelected) {
        if (answer.isCorrect()) {
          score += 1 / correct;
        } else {
          score -= 1 / incorrect; // if incorrect is 0 , is correct is always true.
        }
      }
    }
    return score;
  }

  private double calculateSingleAnswerQuestionScore(List<Answer> answers,
                                                    List<AnswerDto> answerAttempts) {
    if (answerAttempts.isEmpty()) {
      return 0;
    }
    if (answerAttempts.size() > 1) {
      throw new BadRequestException("Expected at most one answer for single answer question");
    }
    Answer correctAnswer = answers.stream().filter(Answer::isCorrect).findAny()
        .orElseThrow(RuntimeException::new);
    AnswerDto attemptedAnswer = answerAttempts.get(0);
    if (correctAnswer.getId().equals(attemptedAnswer.getId())) {
      return 1;
    }
    return -1;
  }

  @Override
  public Page<QuizDto> getAttempts(int page, int limit) {
    PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("createdAt").descending());
    return quizAttemptRepository.findByCreatedBy(principal.getCurrentAuditor().get(),pageRequest)
        .map(this::buildQuizAttempt);
  }

  @Override
  public Page<QuizDto> getQuizStat(int page, Integer limit) {
    PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("createdAt").descending());
    return quizAttemptRepository.findByQuizCreatedBy(principal.getCurrentAuditor().get(),pageRequest)
        .map(this::buildQuizAttempt);
  }

  private QuizDto buildQuizAttempt(QuizAttempt quizAttempt) {
    return QuizDto.builder()
        .id(quizAttempt.getQuiz().getId())
        .published(quizAttempt.getQuiz().isPublished())
        .publishedAt(quizAttempt.getQuiz().getPublishedAt())
        .title(quizAttempt.getQuiz().getTitle())
        .score(quizAttempt.getScore())
        .attemptedBy(quizAttempt.getCreatedBy())
        .questions(quizAttempt.getQuestionAttempts().stream().map(this::buildQuestionAttemptDto).toList())
        .build();
  }

  private QuestionDto buildQuestionAttemptDto(QuestionAttempt questionAttempt) {
    return QuestionDto.builder()
        .id(questionAttempt.getQuestion().getId())
        .text(questionAttempt.getQuestion().getText())
        .multipleAnswer(questionAttempt.getQuestion().isMultipleAnswer())
        .score(questionAttempt.getScore())
        .answers(buildAnswerDto(questionAttempt.getQuestion().getAnswers(),questionAttempt.getSelectedAnswerIds()))
        .build();
  }

  private List<AnswerDto> buildAnswerDto(List<Answer> answers, String selectedAnswerIds) {
    Map<String, Boolean> selected = Arrays.stream(selectedAnswerIds.split(",")).collect(
        Collectors.toMap(s -> s, s -> true));
    return answers.stream().map(
        answer -> buildAnswerDto(answer,selected.getOrDefault(answer.getId(),false))).toList();
  }

  private AnswerDto buildAnswerDto(Answer answer,boolean isSelected) {
    return AnswerDto.builder()
        .text(answer.getText())
        .id(answer.getId())
        .selected(isSelected)
        .build();
  }

}
