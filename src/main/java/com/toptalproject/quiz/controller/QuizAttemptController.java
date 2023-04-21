package com.toptalproject.quiz.controller;

import com.toptalproject.quiz.dto.request.QuizAttemptRequest;
import com.toptalproject.quiz.service.QuizAttemptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/attempt")
public class QuizAttemptController {
  private final QuizAttemptService quizAttemptService;

  public QuizAttemptController(QuizAttemptService quizAttemptService) {
    this.quizAttemptService = quizAttemptService;
  }

  @PostMapping
  public ResponseEntity createAttempt(@RequestBody QuizAttemptRequest quizAttemptRequest){
    quizAttemptService.createQuizAttempt(quizAttemptRequest);
    return ResponseEntity.ok().build();
  }
}
