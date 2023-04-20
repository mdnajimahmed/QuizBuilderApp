package com.toptalproject.quiz.controller;

import com.toptalproject.quiz.dto.request.QuizRequest;
import com.toptalproject.quiz.service.QuizService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quiz")
public class QuizController {
  public QuizController(QuizService quizService) {
    this.quizService = quizService;
  }
  private final QuizService quizService;
  @PostMapping
  public void createQuiz(@Valid @RequestBody QuizRequest request){
    quizService.createQuiz(request);
  }
  @PutMapping("/{id}")
  public void updateQuiz(@PathVariable("id") UUID id , @RequestBody QuizRequest request){
    quizService.updateQuiz(id,request);
  }
}
