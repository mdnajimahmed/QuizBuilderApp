package com.toptalproject.quiz.service;

import com.toptalproject.quiz.dto.AnswerDto;
import com.toptalproject.quiz.dto.QuestionDto;
import com.toptalproject.quiz.dto.QuizDto;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface QuizService {
  @Transactional
  QuizDto createQuiz(QuizDto request);

  @Transactional
  void updateQuiz(UUID id, QuizDto request);

  @Transactional
  void publishQuiz(UUID id);

  @Transactional
  void addQuestion(UUID quizId, QuestionDto request);

  @Transactional
  void updateQuestion(UUID quizId, UUID questionId, QuestionDto request);

  @Transactional
  void deleteQuestion(UUID quizId, UUID questionId);

  @Transactional
  void addAnswerToQuestion(UUID quizId, UUID questionId, AnswerDto request);

  @Transactional
  void updateAnswerToQuestion(UUID quizId, UUID questionId, UUID answerId, AnswerDto request);

  @Transactional
  void deleteAnswer(UUID quizId, UUID questionId, UUID answerId);

  QuizDto getQuizById(UUID id);

  Page<QuizDto> getQuiz(int page, int limit);
}
