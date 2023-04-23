package com.toptalproject.quiz.data.repository;

import com.toptalproject.quiz.data.entity.Quiz;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, UUID> {
  Page<Quiz> findByCreatedByNotAndPublishedTrue(String createdBy, PageRequest pageRequest);

  Page<Quiz> findByCreatedBy(String currentUser, PageRequest pageRequest);
}
