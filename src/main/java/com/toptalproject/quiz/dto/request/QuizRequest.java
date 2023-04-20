package com.toptalproject.quiz.dto.request;

import lombok.Data;

@Data
public class QuizRequest {
  private String title;
  private Boolean isPublished;
}
