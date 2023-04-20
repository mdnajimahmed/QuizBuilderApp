package com.toptalproject.quiz.dto.request;

import com.toptalproject.quiz.dto.ValidQuestion;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class QuizRequest  {
  @NotEmpty(message = "quiz title can not be empty")
  private String title;
  private boolean published;
  @Size(min = 1,max = 10, message = "The quiz is allowed to have at least 1 and at most 10 questions")
  List<@ValidQuestion QuestionRequest> questions = new ArrayList<>();
}
