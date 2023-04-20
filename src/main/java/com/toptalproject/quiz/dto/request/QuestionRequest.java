package com.toptalproject.quiz.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class QuestionRequest {
  @NotEmpty(message = "Question text can not be empty")
  private String text;
  private boolean multipleAnswer;
  @Size(min = 1,max = 5, message = "The question is allowed to have at least 1 and at most 5 answers")
  private List<AnswerRequest> answers = new ArrayList<>();
}
