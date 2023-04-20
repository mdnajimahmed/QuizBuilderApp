package com.toptalproject.quiz.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = QuestionValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target( {ElementType.TYPE_USE })
public @interface ValidQuestion {
  String message() default "Invalid question";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
