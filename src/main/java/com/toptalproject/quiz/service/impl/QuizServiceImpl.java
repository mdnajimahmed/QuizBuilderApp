package com.toptalproject.quiz.service.impl;

import com.toptalproject.quiz.NotFoundException;
import com.toptalproject.quiz.data.entity.Quiz;
import com.toptalproject.quiz.data.repository.QuizRepository;
import com.toptalproject.quiz.dto.request.QuizRequest;
import com.toptalproject.quiz.service.QuizService;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class QuizServiceImpl implements QuizService {
  private final QuizRepository quizRepository;

  QuizServiceImpl(QuizRepository quizRepository) {
    this.quizRepository = quizRepository;
  }

  @Override
  public void createQuiz(QuizRequest request) {
    Quiz quiz = new Quiz();
    quiz.setTitle(request.getTitle());
    quiz.setIsPublished(request.getIsPublished());
    quizRepository.save(quiz);
  }

  @Override
  @Transactional
  public void updateQuiz(UUID id, QuizRequest request) {
    Quiz quiz = quizRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(Quiz.class.getCanonicalName(), id));
    quiz.setTitle(request.getTitle());
    quiz.setIsPublished(request.getIsPublished());
  }
}
