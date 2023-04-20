package com.toptalproject.quiz.service;

import com.toptalproject.quiz.dto.request.QuizRequest;
import java.util.UUID;

public interface QuizService {
  void createQuiz(QuizRequest request);

  void updateQuiz(UUID id, QuizRequest request);
}
