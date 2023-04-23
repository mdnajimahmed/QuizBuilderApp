package com.toptalproject.quiz;


import com.toptalproject.quiz.error.BadRequestException;
import com.toptalproject.quiz.error.NotFoundException;
import com.toptalproject.quiz.error.ValidationErrorResponse;
import com.toptalproject.quiz.error.Violation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
@Slf4j
public class ControllerAdvisor {

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ValidationErrorResponse onConstraintValidationException(
      final ConstraintViolationException exception) {
    final ValidationErrorResponse error = new ValidationErrorResponse();
    for (final ConstraintViolation violation : exception.getConstraintViolations()) {
      error.getViolations().add(
          new Violation(violation.getPropertyPath().toString(), violation.getMessage()));
    }
    return error;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ValidationErrorResponse onMethodArgumentNotValidException(
      final MethodArgumentNotValidException exception) {
    final ValidationErrorResponse error = new ValidationErrorResponse();
    for (final FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
      error.getViolations().add(
          new Violation(fieldError.getField(), fieldError.getDefaultMessage()));
    }
    return error;
  }

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public ValidationErrorResponse onEntityNotFoundException(final NotFoundException exception) {
    final ValidationErrorResponse error = new ValidationErrorResponse();
    error.getViolations().add(
        new Violation(null, exception.getMessage()));
    return error;
  }

  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ValidationErrorResponse onBadRequestException(final BadRequestException exception) {
    final ValidationErrorResponse error = new ValidationErrorResponse();
    error.getViolations().add(
        new Violation(null, exception.getMessage()));
    return error;
  }
}
