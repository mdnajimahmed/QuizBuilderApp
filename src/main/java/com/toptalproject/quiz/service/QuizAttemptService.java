package com.toptalproject.quiz.service;

import com.toptalproject.quiz.dto.QuizDto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;

public interface QuizAttemptService {
  void createQuizAttempt(QuizDto request);

  Page<QuizDto> getAttempts(int page, int limit);
}
