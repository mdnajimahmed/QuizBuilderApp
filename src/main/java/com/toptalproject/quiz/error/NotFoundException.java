package com.toptalproject.quiz.error;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
  private final String entity;
  private final UUID id;

  public NotFoundException(String entity, UUID id) {
    super(String.format("Id %s of type %s not found", id, entity));
    this.entity = entity;
    this.id = id;
  }
}
