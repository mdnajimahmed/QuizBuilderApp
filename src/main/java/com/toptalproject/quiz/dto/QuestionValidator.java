package com.toptalproject.quiz.dto;

import com.toptalproject.quiz.dto.request.AnswerRequest;
import com.toptalproject.quiz.dto.request.QuestionRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class QuestionValidator implements ConstraintValidator<ValidQuestion, QuestionRequest> {

  @Override
  public boolean isValid(QuestionRequest question, ConstraintValidatorContext context) {
    long correctAnsCount = question.getAnswers().stream().filter(AnswerRequest::isCorrect).count();
    String errorMessage = getErrorMessage(correctAnsCount, question.isMultipleAnswer());
    if (errorMessage == null) {
      return true;
    }
    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();
    return false;
  }

  private String getErrorMessage(long correctAnsCount, boolean multipleAnswer) {
    if (correctAnsCount == 0) {
      return "No correct answer provided for the question";
    }
    if(multipleAnswer && correctAnsCount==1){
      return "Expected more than one answer for multiple answer question, found 1";
    }
    if(!multipleAnswer && correctAnsCount>1){
      return "Expected  one answer for single answer question, found multiple";
    }
    return null;
  }
}
