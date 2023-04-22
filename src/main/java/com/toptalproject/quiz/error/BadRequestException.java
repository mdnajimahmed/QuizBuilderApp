package com.toptalproject.quiz.error;

public class BadRequestException extends RuntimeException{
  public BadRequestException(String message) {
    super(message);
  }
}
