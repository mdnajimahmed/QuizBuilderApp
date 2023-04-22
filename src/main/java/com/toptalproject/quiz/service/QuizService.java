package com.toptalproject.quiz.service;

import com.toptalproject.quiz.dto.AnswerDto;
import com.toptalproject.quiz.dto.QuestionDto;
import com.toptalproject.quiz.dto.QuizDto;
import com.toptalproject.quiz.dto.QuizPage;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface QuizService {
  @Transactional
  QuizDto createQuiz(QuizDto request);

  @Transactional
  QuizDto updateQuiz(UUID id, QuizDto request);

  @Transactional
  QuizDto publishQuiz(UUID id);

  @Transactional
  QuizDto addQuestion(UUID quizId, QuestionDto request);

  @Transactional
  QuizDto updateQuestion(UUID quizId, UUID questionId, QuestionDto request);

  @Transactional
  QuizDto deleteQuestion(UUID quizId, UUID questionId);

  @Transactional
  QuizDto addAnswerToQuestion(UUID quizId, UUID questionId, AnswerDto request);

  @Transactional
  QuizDto updateAnswerToQuestion(UUID quizId, UUID questionId, UUID answerId, AnswerDto request);

  @Transactional
  QuizDto deleteAnswer(UUID quizId, UUID questionId, UUID answerId);

  QuizDto getQuizById(UUID id);

  QuizPage getQuiz(int pageNo, int limit);

  @Transactional
  void deleteQuizById(UUID id);
}
