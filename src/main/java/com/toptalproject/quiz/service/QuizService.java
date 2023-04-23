package com.toptalproject.quiz.service;

import com.toptalproject.quiz.dto.OptionDto;
import com.toptalproject.quiz.dto.QuestionDto;
import com.toptalproject.quiz.dto.QuestionInfoDto;
import com.toptalproject.quiz.dto.QuizDto;
import com.toptalproject.quiz.dto.QuizInfoDto;
import com.toptalproject.quiz.dto.QuizPage;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

public interface QuizService {
  @Transactional
  QuizDto createQuiz(QuizDto request);

  @Transactional
  QuizDto updateQuiz(UUID id, QuizInfoDto request);

  @Transactional
  QuizDto publishQuiz(UUID id);

  @Transactional
  QuizDto addQuestion(UUID quizId, QuestionDto request);

  @Transactional
  QuizDto updateQuestion(UUID quizId, UUID questionId, QuestionInfoDto request);

  @Transactional
  QuizDto deleteQuestion(UUID quizId, UUID questionId);

  @Transactional
  QuizDto addOptionToQuestion(UUID quizId, UUID questionId, OptionDto request);

  @Transactional
  QuizDto updateOption(UUID quizId, UUID questionId, UUID optionId, OptionDto request);

  @Transactional
  QuizDto deleteOption(UUID quizId, UUID questionId, UUID optionId);

  QuizDto getQuizById(UUID id);

  QuizPage getQuiz(int pageNo, int limit);

  @Transactional
  void deleteQuizById(UUID id);
}
