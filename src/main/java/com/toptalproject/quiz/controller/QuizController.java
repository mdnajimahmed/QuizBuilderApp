package com.toptalproject.quiz.controller;

import com.toptalproject.quiz.dto.AnswerDto;
import com.toptalproject.quiz.dto.QuestionDto;
import com.toptalproject.quiz.dto.QuizDto;
import com.toptalproject.quiz.service.QuizService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quizzes")
@Validated
public class QuizController {
  public QuizController(QuizService quizService) {
    this.quizService = quizService;
  }

  private final QuizService quizService;

  @GetMapping("/{id}")
  public QuizDto getQuizById(@PathVariable("id") UUID id) {
    return quizService.getQuizById(id);
  }

  @GetMapping
  public Page<QuizDto> getQuiz(
      @RequestParam("page") @Valid @Min (value = 0,message = "Page number needs to be non zero")int page,
      @RequestParam("limit") @Min(1) @Max(100) Integer limit) {
    return quizService.getQuiz(page,limit);
  }

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

  @PostMapping("/{id}/questions/{questionId}/answers")
  public void addAnswer(@PathVariable("id") UUID quizId,
                        @PathVariable("questionId") UUID questionId,
                        @RequestBody AnswerDto request) {
    quizService.addAnswerToQuestion(quizId,questionId,request);
  }

  @PostMapping("/{id}/questions/{questionId}/answers/{answerId}")
  public void updateAnswer(@PathVariable("id") UUID quizId,
                           @PathVariable("questionId") UUID questionId,
                           @PathVariable("answerId") UUID answerId,
                           @RequestBody AnswerDto request) {
    quizService.updateAnswerToQuestion(quizId,questionId,answerId,request);
  }

  @DeleteMapping("/{id}/questions/{questionId}/answers/{answerId}")
  public void deleteAnswer(@PathVariable("id") UUID quizId,
                           @PathVariable("questionId") UUID questionId,
                           @PathVariable("answerId") UUID answerId) {
    quizService.deleteAnswer(quizId,questionId,answerId);
  }
}
