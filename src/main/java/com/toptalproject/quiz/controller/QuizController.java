package com.toptalproject.quiz.controller;

import com.toptalproject.quiz.dto.OptionDto;
import com.toptalproject.quiz.dto.QuestionDto;
import com.toptalproject.quiz.dto.QuizDto;
import com.toptalproject.quiz.dto.QuizPage;
import com.toptalproject.quiz.service.QuizService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
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
  public QuizPage getQuiz(
      @RequestParam("page")
      @Valid @Min(value = 0, message = "Page number needs to be non zero") int pageNo,
      @RequestParam("limit") @Min(1) @Max(100) Integer limit) {
    return quizService.getQuiz(pageNo, limit);
  }

  @PostMapping
  public QuizDto createQuiz(@Valid @RequestBody QuizDto request) {
    return quizService.createQuiz(request);
  }

  @PutMapping("/{id}")
  public QuizDto updateQuiz(@PathVariable("id") UUID id, @Valid @RequestBody QuizDto request) {
    return quizService.updateQuiz(id, request);
  }

  @DeleteMapping("/{id}")
  public void deleteQuizById(@PathVariable("id") UUID id) {
    quizService.deleteQuizById(id);
  }


  @PutMapping("/{id}/publish")
  public QuizDto publishQuiz(@PathVariable("id") UUID id) {
    return quizService.publishQuiz(id);
  }

  @PostMapping("/{id}/questions")
  public QuizDto addQuestion(@PathVariable("id") UUID quizId, @Valid @RequestBody QuestionDto request) {
    return quizService.addQuestion(quizId, request);
  }

  @PutMapping("/{id}/questions/{questionId}")
  public QuizDto updateQuestion(@PathVariable("id") UUID quizId,
                                @PathVariable("questionId") UUID questionId,
                                @Valid @RequestBody QuestionDto request) {
    return quizService.updateQuestion(quizId, questionId, request);
  }

  @DeleteMapping("/{id}/questions/{questionId}")
  public QuizDto deleteQuestion(@PathVariable("id") UUID quizId,
                                @PathVariable("questionId") UUID questionId) {
    return quizService.deleteQuestion(quizId, questionId);
  }

  @PostMapping("/{id}/questions/{questionId}/options")
  public QuizDto addOption(@PathVariable("id") UUID quizId,
                           @PathVariable("questionId") UUID questionId,
                           @Valid @RequestBody OptionDto request) {
    return quizService.addOptionToQuestion(quizId, questionId, request);
  }

  @PutMapping("/{id}/questions/{questionId}/options/{optionId}")
  public QuizDto updateOption(@PathVariable("id") UUID quizId,
                              @PathVariable("questionId") UUID questionId,
                              @PathVariable("optionId") UUID optionId,
                              @Valid @RequestBody OptionDto request) {
    return quizService.updateOption(quizId, questionId, optionId, request);
  }

  @DeleteMapping("/{id}/questions/{questionId}/options/{optionId}")
  public QuizDto deleteOption(@PathVariable("id") UUID quizId,
                              @PathVariable("questionId") UUID questionId,
                              @PathVariable("optionId") UUID optionId) {
    return quizService.deleteOption(quizId, questionId, optionId);
  }
}
