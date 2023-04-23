package com.toptalproject.quiz.error;

import java.util.UUID;
import lombok.Data;

@Data
public class NotFoundException extends RuntimeException {
  private final String entity;
  private final UUID id;

  public NotFoundException(final String entity, final UUID id) {
    super(String.format("Id %s of type %s not found", id, entity));
    this.entity = entity;
    this.id = id;
  }
}
