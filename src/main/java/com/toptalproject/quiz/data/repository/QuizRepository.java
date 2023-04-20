package com.toptalproject.quiz.data.repository;

import com.toptalproject.quiz.data.entity.Quiz;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, UUID> {
}
