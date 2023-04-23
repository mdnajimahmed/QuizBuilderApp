package com.toptalproject.quiz.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
public class OptionDto {
  private UUID id;
  @NotEmpty(message = "Option text can not be empty")
  @Length(max = 255, message = "Maximum 255 character is allowed in option text")
  private String text;
  private Boolean correct;
  private Boolean selected;
}
