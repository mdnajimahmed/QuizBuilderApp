package com.toptalproject.quiz.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class QuizInfoDto {
  @NotEmpty(message = "quiz title can not be empty")
  private String title;
}
