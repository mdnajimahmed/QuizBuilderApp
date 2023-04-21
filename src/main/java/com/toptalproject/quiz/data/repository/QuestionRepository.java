package com.toptalproject.quiz.data.repository;

import com.toptalproject.quiz.data.entity.Question;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
}
