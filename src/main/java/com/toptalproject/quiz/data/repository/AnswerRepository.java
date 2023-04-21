package com.toptalproject.quiz.data.repository;

import com.toptalproject.quiz.data.entity.Answer;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, UUID> {
}
