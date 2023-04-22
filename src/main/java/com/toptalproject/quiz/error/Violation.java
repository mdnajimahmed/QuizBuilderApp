package com.toptalproject.quiz.error;


import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * The Violation.
 */
@Data
@AllArgsConstructor
@SuppressWarnings("java:S1068")
public class Violation {
  /**
   * The fieldName having validation error.
   */
  private final String fieldName;

  /**
   * The error message.
   */
  private final String message;
}
