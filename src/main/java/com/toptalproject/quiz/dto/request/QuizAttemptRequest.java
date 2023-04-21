package com.toptalproject.quiz.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class QuizAttemptRequest {
  @NotNull(message = "Quiz id can not be null")
  private UUID quizId;
  private List<QuestionAttemptRequest> questionAttemptRequests = new ArrayList<>();
}
