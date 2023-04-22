package com.toptalproject.quiz.service;

import com.toptalproject.quiz.dto.QuizDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface QuizAttemptService {
  @Transactional
  QuizDto createQuizAttempt(QuizDto request);

  Page<QuizDto> getAttempts( int page, int limit);

  Page<QuizDto> getQuizStat(UUID id, int page, Integer limit);
}
