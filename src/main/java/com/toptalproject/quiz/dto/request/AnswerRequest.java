package com.toptalproject.quiz.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AnswerRequest {
  @NotEmpty(message = "Answer text can not be empty")
  private String text;
  private boolean correct;
}
