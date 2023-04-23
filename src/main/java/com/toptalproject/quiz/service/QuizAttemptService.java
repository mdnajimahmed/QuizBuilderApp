package com.toptalproject.quiz.service;

import com.toptalproject.quiz.dto.QuizDto;
import com.toptalproject.quiz.dto.QuizPage;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

public interface QuizAttemptService {
  @Transactional
  QuizDto createQuizAttempt(UUID quizId, QuizDto request);

  QuizPage getAttempts(int page, int limit);

  QuizPage getQuizStat(UUID id, int page, int limit);
}
