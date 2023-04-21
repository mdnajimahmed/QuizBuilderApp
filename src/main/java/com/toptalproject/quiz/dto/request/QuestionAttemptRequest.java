package com.toptalproject.quiz.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class QuestionAttemptRequest {
  @NotNull(message = "Question id can not be null")
  private UUID questionId;
  private List<AnswerAttemptRequest> answerAttempts = new ArrayList<>();
}
