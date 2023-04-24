package com.toptalproject.quiz.controller;

import com.toptalproject.quiz.dto.OptionDto;
import com.toptalproject.quiz.dto.QuestionDto;
import com.toptalproject.quiz.dto.QuestionInfoDto;
import com.toptalproject.quiz.dto.QuizDto;
import com.toptalproject.quiz.dto.QuizInfoDto;
import com.toptalproject.quiz.dto.QuizPage;
import com.toptalproject.quiz.service.QuizService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class QuizController {
  public QuizController(final QuizService quizService) {
    this.quizService = quizService;
  }

  private final QuizService quizService;

  @GetMapping("/{id}")
  public QuizDto getQuizById(
      @PathVariable("id") @Valid @NotNull(message = "Quiz id can not be null") final UUID id) {
    return quizService.getQuizById(id);
  }

  @GetMapping
  public QuizPage getQuizzesAuthoredByMe(
      @RequestParam("page") @Valid @Min(value = 0, message = "Page number needs to be non zero")
      final int pageNo, @RequestParam("limit") @Valid @Min(1) @Max(100) final int limit) {
    return quizService.getQuizzesAuthoredByMe(pageNo, limit);
  }

  @GetMapping("/search")
  public QuizPage searchQuiz(
      @RequestParam("page") @Valid @Min(value = 0, message = "Page number needs to be non zero")
      final int pageNo, @Valid @RequestParam("limit") @Min(1) @Max(100) final int limit) {
    return quizService.getAvailableQuizzesToTake(pageNo, limit);
  }


  @PostMapping
  public QuizDto createQuiz(@Valid @RequestBody final QuizDto request) {
    return quizService.createQuiz(request);
  }

  @PutMapping("/{id}")
  public QuizDto updateQuiz(@PathVariable("id") UUID id,
                            @Valid @RequestBody final QuizInfoDto request) {
    return quizService.updateQuiz(id, request);
  }

  @DeleteMapping("/{id}")
  public void deleteQuizById(@PathVariable("id") final UUID id) {
    quizService.deleteQuizById(id);
  }


  @PutMapping("/{id}/publish")
  public QuizDto publishQuiz(@PathVariable("id") final UUID id) {
    return quizService.publishQuiz(id);
  }

  @PostMapping("/{id}/questions")
  public QuizDto addQuestion(@PathVariable("id") final UUID quizId,
                             @Valid @RequestBody final QuestionDto request) {
    return quizService.addQuestion(quizId, request);
  }

  @PutMapping("/{id}/questions/{questionId}")
  public QuizDto updateQuestion(@PathVariable("id") final UUID quizId,
                                @PathVariable("questionId") final UUID questionId,
                                @Valid @RequestBody final QuestionInfoDto request) {
    return quizService.updateQuestion(quizId, questionId, request);
  }

  @DeleteMapping("/{id}/questions/{questionId}")
  public QuizDto deleteQuestion(@PathVariable("id") final UUID quizId,
                                @PathVariable("questionId") final UUID questionId) {
    return quizService.deleteQuestion(quizId, questionId);
  }

  @PostMapping("/{id}/questions/{questionId}/options")
  public QuizDto addOption(@PathVariable("id") final UUID quizId,
                           @PathVariable("questionId") final UUID questionId,
                           @Valid @RequestBody final OptionDto request) {
    return quizService.addOptionToQuestion(quizId, questionId, request);
  }

  @PutMapping("/{id}/questions/{questionId}/options/{optionId}")
  public QuizDto updateOption(@PathVariable("id") final UUID quizId,
                              @PathVariable("questionId") final UUID questionId,
                              @PathVariable("optionId") final UUID optionId,
                              @Valid @RequestBody final OptionDto request) {
    return quizService.updateOption(quizId, questionId, optionId, request);
  }

  @DeleteMapping("/{id}/questions/{questionId}/options/{optionId}")
  public QuizDto deleteOption(@PathVariable("id") final UUID quizId,
                              @PathVariable("questionId") final UUID questionId,
                              @PathVariable("optionId") final UUID optionId) {
    return quizService.deleteOption(quizId, questionId, optionId);
  }
}
