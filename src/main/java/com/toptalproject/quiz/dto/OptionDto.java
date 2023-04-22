package com.toptalproject.quiz.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OptionDto {
  private UUID id;
  @NotEmpty(message = "Option text can not be empty")
  private String text;
  private Boolean correct;
  private Boolean selected;
}
