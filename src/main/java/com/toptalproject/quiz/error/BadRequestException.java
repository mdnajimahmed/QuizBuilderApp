package com.toptalproject.quiz.error;

public class BadRequestException extends RuntimeException{
  public BadRequestException(final String message) {
    super(message);
  }
}
