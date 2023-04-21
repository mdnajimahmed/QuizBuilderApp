package com.toptalproject.quiz.controller;

import com.toptalproject.quiz.dto.AnswerDto;
import com.toptalproject.quiz.dto.QuestionDto;
import com.toptalproject.quiz.dto.QuizDto;
import com.toptalproject.quiz.service.QuizService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
  public QuizDto createQuiz(@Valid @RequestBody QuizDto request) {
    return quizService.createQuiz(request);
  }

  @PutMapping("/{id}")
  public void updateQuiz(@PathVariable("id") UUID id, @RequestBody QuizDto request) {
    quizService.updateQuiz(id, request);
  }

  @PostMapping("/{id}/publish")
  public void publishQuiz(@PathVariable("id") UUID id) {
    quizService.publishQuiz(id);
  }

  @PostMapping("/{id}/questions")
  public void addQuestion(@PathVariable("id") UUID quizId, @RequestBody QuestionDto request) {
    quizService.addQuestion(quizId,request);
  }

  @PostMapping("/{id}/questions/{questionId}")
  public void updateQuestion(@PathVariable("id") UUID quizId,
                             @PathVariable("questionId") UUID questionId,
                             @RequestBody QuestionDto request) {
    quizService.updateQuestion(quizId,questionId,request);
  }

  @DeleteMapping("/{id}/questions/{questionId}")
  public void deleteQuestion(@PathVariable("id") UUID quizId,
                             @PathVariable("questionId") UUID questionId) {
    quizService.deleteQuestion(quizId,questionId);
  }

  @PostMapping("/{id}/questions/{questionId}/answer")
  public void addAnswer(@PathVariable("id") UUID quizId,
                        @PathVariable("questionId") UUID questionId,
                        @RequestBody AnswerDto request) {
    quizService.addAnswerToQuestion(quizId,questionId,request);
  }

  @PostMapping("/{id}/questions/{questionId}/answer/{answerId}")
  public void updateAnswer(@PathVariable("id") UUID quizId,
                           @PathVariable("questionId") UUID questionId,
                           @PathVariable("answerId") UUID answerId,
                           @RequestBody AnswerDto request) {
    quizService.updateAnswerToQuestion(quizId,questionId,answerId,request);
  }

  @DeleteMapping("/{id}/questions/{questionId}/answer/{answerId}")
  public void deleteAnswer(@PathVariable("id") UUID quizId,
                           @PathVariable("questionId") UUID questionId,
                           @PathVariable("answerId") UUID answerId) {
    quizService.deleteAnswer(quizId,questionId,answerId);
  }
}
