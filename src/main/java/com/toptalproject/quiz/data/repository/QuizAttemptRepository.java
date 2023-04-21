package com.toptalproject.quiz.data.repository;

import com.toptalproject.quiz.data.entity.Quiz;
import com.toptalproject.quiz.data.entity.QuizAttempt;
import com.toptalproject.quiz.dto.QuizDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {
  boolean existsByQuizAndCreatedBy(Quiz quiz, String createdBy);

  Page<QuizAttempt> findByCreatedBy(String createdBy, PageRequest pageRequest);
}
