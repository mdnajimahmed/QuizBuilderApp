package com.toptalproject.quiz.service.impl;

import com.toptalproject.quiz.data.entity.Answer;
import com.toptalproject.quiz.data.entity.Question;
import com.toptalproject.quiz.data.entity.Quiz;
import com.toptalproject.quiz.dto.AnswerDto;
import com.toptalproject.quiz.dto.QuestionDto;
import com.toptalproject.quiz.error.BadRequestException;
import com.toptalproject.quiz.error.NotFoundException;
import com.toptalproject.quiz.data.repository.QuizRepository;
import com.toptalproject.quiz.dto.QuizDto;
import com.toptalproject.quiz.service.QuizService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
class QuizServiceImpl implements QuizService {
  private final QuizRepository quizRepository;
  private final AuditorAware<String> principal;

  QuizServiceImpl(QuizRepository quizRepository, AuditorAware<String> principal) {
    this.quizRepository = quizRepository;
    this.principal = principal;
  }

  @Override
  public QuizDto createQuiz(QuizDto request) {
    Quiz quiz = new Quiz();
    quiz.setTitle(request.getTitle());
    quiz.setPublished(request.getPublished());
    if (request.getPublished()) {
      quiz.setPublishedAt(LocalDateTime.now());
    }
    request.getQuestions().forEach(q -> quiz.addQuestion(mapToQuestion(q)));
    quizRepository.save(quiz);
    return buildQuizDto(quiz);
  }


  @Override
  public void updateQuiz(UUID id, QuizDto request) {
    Quiz quiz = selectQuizForUpdate(id);
    quiz.setTitle(request.getTitle());
  }

  @Override
  public void publishQuiz(UUID id) {
    Quiz quiz = selectQuizForUpdate(id);
    quiz.setPublished(true);
    quiz.setPublishedAt(LocalDateTime.now());
  }

  @Override
  public void addQuestion(UUID quizId, QuestionDto request) {
    Quiz quiz = selectQuizForUpdate(quizId);
    if (quiz.getQuestions().size() > 10) {
      throw new BadRequestException("The quiz already has a maximum number of 10 questions");
    }
    quiz.addQuestion(mapToQuestion(request));
  }

  @Override
  public void updateQuestion(UUID quizId, UUID questionId, QuestionDto request) {
    Question question = selectQuestionForUpdate(quizId, questionId);
    question.setText(request.getText());
    question.setMultipleAnswer(request.getMultipleAnswer());
    validateQuestion(question);
  }

  @Override
  public void deleteQuestion(UUID quizId, UUID questionId) {
    Question question = selectQuestionForUpdate(quizId, questionId);
    if (question.getQuiz().getQuestions().size() == 1) {
      throw new BadRequestException("A quiz must have at least one question");
    }
    question.getQuiz().removeQuestion(question);
  }

  @Override
  public void addAnswerToQuestion(UUID quizId, UUID questionId, AnswerDto request) {
    Question question = selectQuestionForUpdate(quizId, questionId);
    if (question.getAnswers().size() > 5) {
      throw new BadRequestException("The question already has a maximum number of 5 answers");
    }
    question.addAnswer(mapToAnswer(request));
    validateQuestion(question);
  }

  @Override
  public void updateAnswerToQuestion(UUID quizId, UUID questionId, UUID answerId,
                                     AnswerDto request) {
    Answer answer = selectAnswerForUpdate(quizId, questionId, answerId);
    answer.setCorrect(request.getCorrect());
    answer.setText(request.getText());
    validateQuestion(answer.getQuestion());
  }

  @Override
  public void deleteAnswer(UUID quizId, UUID questionId, UUID answerId) {
    Answer answer = selectAnswerForUpdate(quizId, questionId, answerId);
    if (answer.getQuestion().getAnswers().size() < 3) {
      throw new BadRequestException("Question should have at least 2 answers");
    }
    Question question = answer.getQuestion();
    answer.getQuestion().removeAnswer(answer);
    validateQuestion(question);
  }

  @Override
  public QuizDto getQuizById(UUID id) {
    Quiz quiz = quizRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(Quiz.class.getCanonicalName(), id));
    return buildQuizDto(quiz);
  }

  @Override
  public Page<QuizDto> getQuiz(int page, int limit) {
    PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("createdAt").descending());
    return quizRepository.findAll(pageRequest).map(this::buildQuizDto);
  }

  @Override
  public void deleteQuizById(UUID id) {
    Quiz quiz = selectQuizForUpdate(id);
    quizRepository.delete(quiz);
  }

  private Question mapToQuestion(QuestionDto questionRequest) {
    Question question = new Question();
    question.setText(questionRequest.getText());
    question.setMultipleAnswer(questionRequest.getMultipleAnswer());
    questionRequest.getAnswers()
        .forEach(answerRequest -> question.addAnswer(mapToAnswer(answerRequest)));
    validateQuestion(question);
    return question;
  }

  private Answer mapToAnswer(AnswerDto answerRequest) {
    Answer answer = new Answer();
    answer.setText(answerRequest.getText());
    answer.setCorrect(answerRequest.getCorrect());
    return answer;
  }

  private void validateQuestion(Question question) {
    long correctAnsCount = question.getAnswers().stream().filter(Answer::isCorrect).count();
    if (correctAnsCount == 0) {
      throw new BadRequestException("No correct answer provided for the question");
    }
    if (question.isMultipleAnswer() && correctAnsCount == 1) {
      throw new BadRequestException("Multiple answer question requires at least 2 correct answer");
    }
    if (!question.isMultipleAnswer() && correctAnsCount > 1) {
      throw new BadRequestException(
          "Single answer question can not have more than one correct answer");
    }
  }

  private QuizDto buildQuizDto(Quiz quiz) {
    return QuizDto.builder()
        .id(quiz.getId())
        .published(quiz.isPublished())
        .publishedAt(quiz.getPublishedAt())
        .title(quiz.getTitle())
        .questions(quiz.getQuestions().stream().map(this::buildQuestionDto).toList())
        .build();
  }

  private QuestionDto buildQuestionDto(Question question) {
    return QuestionDto.builder()
        .id(question.getId())
        .text(question.getText())
        .multipleAnswer(question.isMultipleAnswer())
        .answers(question.getAnswers().stream().map(this::buildAnswerDto).toList())
        .build();
  }

  private AnswerDto buildAnswerDto(Answer answer) {
    return AnswerDto.builder()
        .correct(answer.isCorrect())
        .text(answer.getText())
        .id(answer.getId())
        .build();
  }

  private Quiz selectQuizForUpdate(UUID id) {
    Quiz quiz = quizRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(Quiz.class.getCanonicalName(), id));
    if (quiz.isPublished()) {
      throw new BadRequestException("A published quiz can not be updated");
    }
    String aud = principal.getCurrentAuditor().orElse(null);
    if (!quiz.getCreatedBy().equals(aud)) {
      throw new BadRequestException("Quiz ownership check failed");
    }
    return quiz;
  }

  private Question selectQuestionForUpdate(UUID quizId, UUID questionId) {
    Quiz quiz = selectQuizForUpdate(quizId);
    Question question =
        quiz.getQuestions().stream().filter(q -> q.getId().equals(questionId)).findAny()
            .orElseThrow(
                () -> new NotFoundException(Question.class.getCanonicalName(), questionId));
    return question;
  }

  private Answer selectAnswerForUpdate(UUID quizId, UUID questionId, UUID answerId) {
    Question question = selectQuestionForUpdate(quizId, questionId);
    Answer answer = question.getAnswers().stream().filter(a -> a.getId().equals(answerId)).findAny()
        .orElseThrow(() -> new NotFoundException(Answer.class.getCanonicalName(), answerId));
    return answer;
  }

}
