package com.toptalproject.quiz.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class QuestionInfoDto {
  @NotEmpty(message = "Question text can not be empty")
  private String text;
}
