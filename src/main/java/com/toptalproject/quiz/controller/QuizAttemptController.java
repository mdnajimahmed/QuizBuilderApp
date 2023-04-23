package com.toptalproject.quiz.controller;

import com.toptalproject.quiz.dto.QuizDto;
import com.toptalproject.quiz.dto.QuizPage;
import com.toptalproject.quiz.service.QuizAttemptService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/attempts")
public class QuizAttemptController {
  private final QuizAttemptService quizAttemptService;

  public QuizAttemptController(QuizAttemptService quizAttemptService) {
    this.quizAttemptService = quizAttemptService;
  }

  @PostMapping("/${quizId}")
  public QuizDto createAttempt(
      @Valid @NotNull(message = "Quiz id must be not-null") UUID quizId,
      @RequestBody QuizDto quizAttemptRequest) {
    return quizAttemptService.createQuizAttempt(quizId, quizAttemptRequest);
  }

  @GetMapping
  public QuizPage getAttempts(
      @RequestParam("page")
      @Valid @Min(value = 0, message = "Page number needs to be non zero") int page,
      @RequestParam("limit") @Min(1) @Max(100) int limit) {
    return quizAttemptService.getAttempts(page, limit);
  }

  @GetMapping("/stat/{quizId}")
  public QuizPage getQuizStat(
      @PathVariable("quizId") @Valid @NotNull(message = "Quiz id can not be null") UUID quizId,
      @RequestParam("page")
      @Valid @Min(value = 0, message = "Page number needs to be non zero") int pageNo,
      @RequestParam("limit") @Min(1) @Max(100) Integer limit) {
    return quizAttemptService.getQuizStat(quizId, pageNo, limit);
  }
}
