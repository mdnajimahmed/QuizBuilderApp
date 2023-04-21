package com.toptalproject.quiz.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;

@Data
public class AnswerAttemptRequest {
  @NotNull(message = "Answer id can not be null")
  private UUID answerId;
}
