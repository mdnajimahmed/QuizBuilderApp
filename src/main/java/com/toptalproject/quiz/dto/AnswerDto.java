package com.toptalproject.quiz.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnswerDto {
  private UUID id;
  @NotEmpty(message = "Answer text can not be empty")
  private String text;
  private boolean correct;
}
