package com.toptalproject.quiz.error;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The ValidationErrorResponse.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponse {
  /**
   * The list of violations.
   */
  @SuppressWarnings("java:S1068")
  private List<Violation> violations = new ArrayList<>();
}
