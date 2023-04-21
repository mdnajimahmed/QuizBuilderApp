package com.toptalproject.quiz.data.repository;

import com.toptalproject.quiz.data.entity.Quiz;
import com.toptalproject.quiz.data.entity.QuizAttempt;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {
  boolean existsByQuizAndCreatedBy(Quiz quiz, String createdBy);
}
