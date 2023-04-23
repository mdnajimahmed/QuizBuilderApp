package com.toptalproject.quiz.error;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponse {
  private List<Violation> violations = new ArrayList<>();
}
