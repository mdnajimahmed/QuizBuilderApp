package com.toptalproject.quiz.service;

import com.toptalproject.quiz.dto.request.QuizAttemptRequest;

public interface QuizAttemptService {
  void createQuizAttempt(QuizAttemptRequest request);
}
