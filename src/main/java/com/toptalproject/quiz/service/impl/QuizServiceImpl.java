package com.toptalproject.quiz.service.impl;

import com.toptalproject.quiz.data.entity.Option;
import com.toptalproject.quiz.data.entity.Question;
import com.toptalproject.quiz.data.entity.Quiz;
import com.toptalproject.quiz.data.repository.OptionRepository;
import com.toptalproject.quiz.data.repository.QuestionRepository;
import com.toptalproject.quiz.data.repository.QuizAttemptRepository;
import com.toptalproject.quiz.data.repository.QuizRepository;
import com.toptalproject.quiz.dto.OptionDto;
import com.toptalproject.quiz.dto.QuestionDto;
import com.toptalproject.quiz.dto.QuestionInfoDto;
import com.toptalproject.quiz.dto.QuizDto;
import com.toptalproject.quiz.dto.QuizInfoDto;
import com.toptalproject.quiz.dto.QuizPage;
import com.toptalproject.quiz.error.BadRequestException;
import com.toptalproject.quiz.error.NotFoundException;
import com.toptalproject.quiz.service.QuizService;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
class QuizServiceImpl implements QuizService {
  private final QuizRepository quizRepository;
  private final QuestionRepository questionRepository;
  private final OptionRepository optionRepository;
  private final AuditorAware<String> principal;

  private final QuizAttemptRepository quizAttemptRepository;

  QuizServiceImpl(final QuizRepository quizRepository, final QuestionRepository questionRepository,
                  final OptionRepository optionRepository, final AuditorAware<String> principal,
                  final QuizAttemptRepository quizAttemptRepository) {
    this.quizRepository = quizRepository;
    this.questionRepository = questionRepository;
    this.optionRepository = optionRepository;
    this.principal = principal;
    this.quizAttemptRepository = quizAttemptRepository;
  }

  @Override
  public QuizDto createQuiz(final QuizDto request) {
    log.info("Creating quiz, payload = {}", request);
    final Quiz quiz = new Quiz();
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
  public QuizDto updateQuiz(final UUID id, final QuizInfoDto request) {
    log.info("Updating quiz = {}, payload = {}", id, request);
    final Quiz quiz = selectQuizForUpdate(id);
    quiz.setTitle(request.getTitle());
    return buildQuizDto(quiz);
  }

  @Override
  public QuizDto publishQuiz(final UUID id) {
    log.info("Publish quiz = {}", id);
    final Quiz quiz = selectQuizForUpdate(id);
    quiz.setPublished(true);
    quiz.setPublishedAt(LocalDateTime.now());
    return buildQuizDto(quiz);
  }

  @Override
  public QuizDto addQuestion(final UUID quizId, final QuestionDto request) {
    log.info("Adding question to quiz = {}, payload = {}", quizId, request);
    final Quiz quiz = selectQuizForUpdate(quizId);
    if (quiz.getQuestions().size() > 10) {
      throw new BadRequestException("The quiz already has a maximum number of 10 questions");
    }
    final Question question = mapToQuestion(request);
    quiz.addQuestion(question);
    questionRepository.save(question);
    return buildQuizDto(quiz);
  }

  @Override
  public QuizDto updateQuestion(
      final UUID quizId, final UUID questionId,
      final QuestionInfoDto request) {
    log.info("updating question under quiz = {}, question = {}, payload = {}", quizId, questionId,
        request);
    final Question question = selectQuestionForUpdate(quizId, questionId);
    question.setText(request.getText());
    return buildQuizDto(question.getQuiz());
  }

  @Override
  public QuizDto deleteQuestion(final UUID quizId, final UUID questionId) {
    log.info("Deleting question, quizId = {}, questionId = {}", quizId, questionId);
    final Question question = selectQuestionForUpdate(quizId, questionId);
    if (question.getQuiz().getQuestions().size() == 1) {
      throw new BadRequestException("A quiz must have at least one question");
    }
    final Quiz quiz = question.getQuiz();
    quiz.removeQuestion(question);
    return buildQuizDto(quiz);
  }

  @Override
  public QuizDto addOptionToQuestion(
      final UUID quizId, final UUID questionId,
      final OptionDto request) {
    log.info("adding option to question, quiz = {}, questionId = {}, request = {}", quizId,
        questionId, request);
    final Question question = selectQuestionForUpdate(quizId, questionId);
    if (question.getOptions().size() > 5) {
      throw new BadRequestException("The question already has a maximum number of 5 options");
    }
    final Option option = mapToOption(request);
    question.addOption(option);
    optionRepository.save(option);
    return buildQuizDto(question.getQuiz());
  }

  @Override
  public QuizDto updateOption(
      final UUID quizId, final UUID questionId, final UUID optionId,
      final OptionDto request) {
    log.info("Updating option, quizId={},questionId={},optionId={},request = {}", quizId,
        questionId, optionId, request);
    final Option option = selectOptionForUpdate(quizId, questionId, optionId);
    option.setCorrect(request.getCorrect());
    option.setText(request.getText());
    validateQuestion(option.getQuestion());
    return buildQuizDto(option.getQuestion().getQuiz());
  }

  @Override
  public QuizDto deleteOption(final UUID quizId, final UUID questionId, final UUID optionId) {
    log.info("deleting option, quizId={},questionId={},optionId={}", quizId,
        questionId, optionId);
    final Option option = selectOptionForUpdate(quizId, questionId, optionId);
    if (option.getQuestion().getOptions().size() < 2) {
      throw new BadRequestException("Question should have at least 1 answers");
    }
    final Question question = option.getQuestion();
    option.getQuestion().removeOption(option);
    validateQuestion(question);
    return buildQuizDto(question.getQuiz());
  }

  @Override
  public QuizDto getQuizById(final UUID id) {
    log.info("Getting quiz by id = {}", id);
    final Quiz quiz = quizRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(Quiz.class.getCanonicalName(), id));
    return buildQuizDto(quiz);
  }

  @Override
  public QuizPage getQuizzes(final boolean isAuthoredByMe, final int pageNo, final int limit) {
    log.info("getting quizzes with params isAuthoredByMe={}, pageNo={},limit = {}", isAuthoredByMe,
        pageNo, limit);
    final String sortBy = isAuthoredByMe ? "updatedAt" : "publishedAt";
    final PageRequest pageRequest = PageRequest.of(pageNo, limit, Sort.by(sortBy).descending());
    final String currentUser =
        principal.getCurrentAuditor()
            .orElseThrow(() -> new NotFoundException("LOGGED_IN_USER", null));
    final Page<Quiz> currentQuizPage =
        isAuthoredByMe ? quizRepository.findByCreatedBy(currentUser, pageRequest) :
            quizRepository.findByCreatedByNotAndPublishedTrue(currentUser, pageRequest);
    final Page<QuizDto> currentQuizDtoPage = currentQuizPage.map(this::buildQuizDto);
    return new QuizPage(currentQuizDtoPage.getContent(), pageNo, currentQuizDtoPage.getTotalPages(),
        limit);
  }

  @Override
  public void deleteQuizById(final UUID id) {
    log.info("Deleting quiz by id = {}", id);
    final Quiz quiz = selectQuizForDelete(id);
    quizRepository.delete(quiz);
  }


  private Question mapToQuestion(final QuestionDto questionRequest) {
    final Question question = new Question();
    question.setText(questionRequest.getText());
    questionRequest.getOptions()
        .forEach(selectedOption -> question.addOption(mapToOption(selectedOption)));
    validateQuestion(question);
    return question;
  }

  private Option mapToOption(final OptionDto optionDto) {
    final Option option = new Option();
    option.setText(optionDto.getText());
    option.setCorrect(optionDto.getCorrect());
    return option;
  }

  private void validateQuestion(final Question question) {
    final long correctAnsCount = question.getOptions().stream().filter(Option::isCorrect).count();
    log.debug("correctAnsCount = {}", correctAnsCount);
    if (correctAnsCount == 0) {
      throw new BadRequestException("No correct answer provided for the question");
    }
  }

  private QuizDto buildQuizDto(final Quiz quiz) {
    return QuizDto.builder()
        .id(quiz.getId())
        .published(quiz.isPublished())
        .publishedAt(quiz.getPublishedAt())
        .title(quiz.getTitle())
        .questions(quiz.getQuestions().stream().map(this::buildQuestionDto).toList())
        .build();
  }

  private QuestionDto buildQuestionDto(final Question question) {
    return QuestionDto.builder()
        .id(question.getId())
        .text(question.getText())
        .options(question.getOptions().stream().map(this::buildOptionDto).toList())
        .build();
  }

  private OptionDto buildOptionDto(final Option option) {
    return OptionDto.builder()
        .correct(option.isCorrect())
        .text(option.getText())
        .id(option.getId())
        .build();
  }

  private Quiz selectQuizForUpdate(final UUID id) {
    final Quiz quiz = quizRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(Quiz.class.getCanonicalName(), id));
    if (quiz.isPublished()) {
      throw new BadRequestException("A published quiz can not be updated");
    }
    final String aud =
        principal.getCurrentAuditor()
            .orElseThrow(() -> new NotFoundException("LOGGED_IN_USER", null));

    if (!quiz.getCreatedBy().equals(aud)) {
      throw new BadRequestException("Quiz not created by the requester");
    }
    return quiz;
  }

  private Question selectQuestionForUpdate(final UUID quizId, final UUID questionId) {
    final Quiz quiz = selectQuizForUpdate(quizId);
    return quiz.getQuestions().stream().filter(q -> q.getId().equals(questionId)).findAny()
        .orElseThrow(
            () -> new NotFoundException(Question.class.getCanonicalName(), questionId));
  }

  private Option selectOptionForUpdate(
      final UUID quizId, final UUID questionId,
      final UUID optionId) {
    final Question question = selectQuestionForUpdate(quizId, questionId);
    return question.getOptions().stream().filter(a -> a.getId().equals(optionId)).findAny()
        .orElseThrow(() -> new NotFoundException(Option.class.getCanonicalName(), optionId));
  }

  private Quiz selectQuizForDelete(UUID id) {
    final Quiz quiz = quizRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(Quiz.class.getCanonicalName(), id));
    final String aud =
        principal.getCurrentAuditor()
            .orElseThrow(() -> new NotFoundException("LOGGED_IN_USER", null));
    if (!quiz.getCreatedBy().equals(aud)) {
      throw new BadRequestException("Quiz not created by the requester");
    }
    if (quizAttemptRepository.existsByQuiz(quiz)) {
      throw new BadRequestException("Quiz already taken by other users");
    }
    return quiz;
  }

}
