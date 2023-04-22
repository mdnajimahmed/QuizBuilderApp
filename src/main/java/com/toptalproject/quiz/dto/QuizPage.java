package com.toptalproject.quiz.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuizPage {
  private List<QuizDto> quizzes;
  private int pageNo;
  private int totalPage;
  private int pageLimit;
}
