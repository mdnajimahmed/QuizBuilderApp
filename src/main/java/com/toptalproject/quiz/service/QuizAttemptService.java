package com.toptalproject.quiz.service;

import com.toptalproject.quiz.dto.QuizDto;

public interface QuizAttemptService {
  void createQuizAttempt(QuizDto request);
}
