package com.toptalproject.quiz.service.impl;

import com.toptalproject.quiz.data.entity.Option;
import com.toptalproject.quiz.data.entity.Question;
import com.toptalproject.quiz.data.entity.Quiz;
import com.toptalproject.quiz.data.repository.OptionRepository;
import com.toptalproject.quiz.data.repository.QuestionRepository;
import com.toptalproject.quiz.dto.OptionDto;
import com.toptalproject.quiz.dto.QuestionDto;
import com.toptalproject.quiz.dto.QuestionInfoDto;
import com.toptalproject.quiz.dto.QuizInfoDto;
import com.toptalproject.quiz.dto.QuizPage;
import com.toptalproject.quiz.error.BadRequestException;
import com.toptalproject.quiz.error.NotFoundException;
import com.toptalproject.quiz.data.repository.QuizRepository;
import com.toptalproject.quiz.dto.QuizDto;
import com.toptalproject.quiz.service.QuizService;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
class QuizServiceImpl implements QuizService {
  private final QuizRepository quizRepository;
  private final QuestionRepository questionRepository;
  private final OptionRepository optionRepository;
  private final AuditorAware<String> principal;

  QuizServiceImpl(QuizRepository quizRepository, QuestionRepository questionRepository,
                  OptionRepository optionRepository, AuditorAware<String> principal) {
    this.quizRepository = quizRepository;
    this.questionRepository = questionRepository;
    this.optionRepository = optionRepository;
    this.principal = principal;
  }

  @Override
  public QuizDto createQuiz(QuizDto request) {
    Quiz quiz = new Quiz();
    quiz.setTitle(request.getTitle());
    quiz.setPublished(request.getPublished());
    if (Boolean.TRUE.equals(request.getPublished())) {
      quiz.setPublishedAt(LocalDateTime.now());
    }
    request.getQuestions().forEach(q -> quiz.addQuestion(mapToQuestion(q)));
    quizRepository.save(quiz);
    return buildQuizDto(quiz);
  }


  @Override
  public QuizDto updateQuiz(UUID id, QuizInfoDto request) {
    Quiz quiz = selectQuizForUpdate(id);
    quiz.setTitle(request.getTitle());
    return buildQuizDto(quiz);
  }

  @Override
  public QuizDto publishQuiz(UUID id) {
    Quiz quiz = selectQuizForUpdate(id);
    quiz.setPublished(true);
    quiz.setPublishedAt(LocalDateTime.now());
    return buildQuizDto(quiz);
  }

  @Override
  public QuizDto addQuestion(UUID quizId, QuestionDto request) {
    Quiz quiz = selectQuizForUpdate(quizId);
    if (quiz.getQuestions().size() > 10) {
      throw new BadRequestException("The quiz already has a maximum number of 10 questions");
    }
    Question question = mapToQuestion(request);
    quiz.addQuestion(question);
    questionRepository.save(question);
    return buildQuizDto(quiz);
  }

  @Override
  public QuizDto updateQuestion(UUID quizId, UUID questionId, QuestionInfoDto request) {
    Question question = selectQuestionForUpdate(quizId, questionId);
    question.setText(request.getText());
    return buildQuizDto(question.getQuiz());
  }

  @Override
  public QuizDto deleteQuestion(UUID quizId, UUID questionId) {
    Question question = selectQuestionForUpdate(quizId, questionId);
    if (question.getQuiz().getQuestions().size() == 1) {
      throw new BadRequestException("A quiz must have at least one question");
    }
    Quiz quiz = question.getQuiz();
    quiz.removeQuestion(question);
    return buildQuizDto(quiz);
  }

  @Override
  public QuizDto addOptionToQuestion(UUID quizId, UUID questionId, OptionDto request) {
    Question question = selectQuestionForUpdate(quizId, questionId);
    if (question.getOptions().size() > 5) {
      throw new BadRequestException("The question already has a maximum number of 5 options");
    }
    Option option = mapToOption(request);
    question.addOption(option);
    optionRepository.save(option);
    return buildQuizDto(question.getQuiz());
  }

  @Override
  public QuizDto updateOption(UUID quizId, UUID questionId, UUID optionId,
                              OptionDto request) {
    Option option = selectOptionForUpdate(quizId, questionId, optionId);
    option.setCorrect(request.getCorrect());
    option.setText(request.getText());
    validateQuestion(option.getQuestion());
    return buildQuizDto(option.getQuestion().getQuiz());
  }

  @Override
  public QuizDto deleteOption(UUID quizId, UUID questionId, UUID optionId) {
    Option option = selectOptionForUpdate(quizId, questionId, optionId);
    if (option.getQuestion().getOptions().size() < 2) {
      throw new BadRequestException("Question should have at least 1 answers");
    }
    Question question = option.getQuestion();
    option.getQuestion().removeOption(option);
    validateQuestion(question);
    return buildQuizDto(question.getQuiz());
  }

  @Override
  public QuizDto getQuizById(UUID id) {
    Quiz quiz = quizRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(Quiz.class.getCanonicalName(), id));
    return buildQuizDto(quiz);
  }

  @Override
  public QuizPage getQuizzes(boolean isAuthoredByMe, int pageNo, int limit) {
    String sortBy = isAuthoredByMe ? "updatedAt" : "publishedAt";
    PageRequest pageRequest = PageRequest.of(pageNo, limit, Sort.by(sortBy).descending());
    String currentUser =
        principal.getCurrentAuditor()
            .orElseThrow(() -> new NotFoundException("LOGGED_IN_USER", null));
    Page<Quiz> currentQuizPage =
        isAuthoredByMe ? quizRepository.findByCreatedBy(currentUser, pageRequest) :
            quizRepository.findByCreatedByNotAndPublishedTrue(currentUser, pageRequest);
    Page<QuizDto> currentQuizDtoPage = currentQuizPage.map(this::buildQuizDto);
    return new QuizPage(currentQuizDtoPage.getContent(), pageNo, currentQuizDtoPage.getTotalPages(),
        limit);
  }

  @Override
  public void deleteQuizById(UUID id) {
    Quiz quiz = selectQuizForUpdate(id);
    quizRepository.delete(quiz);
  }

  private Question mapToQuestion(QuestionDto questionRequest) {
    Question question = new Question();
    question.setText(questionRequest.getText());
    questionRequest.getOptions()
        .forEach(selectedOption -> question.addOption(mapToOption(selectedOption)));
    validateQuestion(question);
    return question;
  }

  private Option mapToOption(OptionDto optionDto) {
    Option option = new Option();
    option.setText(optionDto.getText());
    option.setCorrect(optionDto.getCorrect());
    return option;
  }

  private void validateQuestion(Question question) {
    long correctAnsCount = question.getOptions().stream().filter(Option::isCorrect).count();
    if (correctAnsCount == 0) {
      throw new BadRequestException("No correct answer provided for the question");
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
        .options(question.getOptions().stream().map(this::buildOptionDto).toList())
        .build();
  }

  private OptionDto buildOptionDto(Option option) {
    return OptionDto.builder()
        .correct(option.isCorrect())
        .text(option.getText())
        .id(option.getId())
        .build();
  }

  private Quiz selectQuizForUpdate(UUID id) {
    Quiz quiz = quizRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(Quiz.class.getCanonicalName(), id));
    if (quiz.isPublished()) {
      throw new BadRequestException("A published quiz can not be updated");
    }
    String aud =
        principal.getCurrentAuditor()
            .orElseThrow(() -> new NotFoundException("LOGGED_IN_USER", null));

    if (!quiz.getCreatedBy().equals(aud)) {
      throw new BadRequestException("Quiz ownership check failed");
    }
    return quiz;
  }

  private Question selectQuestionForUpdate(UUID quizId, UUID questionId) {
    Quiz quiz = selectQuizForUpdate(quizId);
    return quiz.getQuestions().stream().filter(q -> q.getId().equals(questionId)).findAny()
        .orElseThrow(
            () -> new NotFoundException(Question.class.getCanonicalName(), questionId));
  }

  private Option selectOptionForUpdate(UUID quizId, UUID questionId, UUID optionId) {
    Question question = selectQuestionForUpdate(quizId, questionId);
    return question.getOptions().stream().filter(a -> a.getId().equals(optionId)).findAny()
        .orElseThrow(() -> new NotFoundException(Option.class.getCanonicalName(), optionId));
  }

}
