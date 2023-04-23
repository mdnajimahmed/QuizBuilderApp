package com.toptalproject.quiz.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
public class QuestionDto {
  private UUID id;
  @NotEmpty(message = "Question text can not be empty")
  @Length(max = 255, message = "Maximum 255 character is allowed in question text")
  private String text;
  private Boolean skipped;
  private Double score;
  @Size(min = 1, max = 5, message = "A question is allowed to have 1 to 5 options")
  @NotNull(message = "Options can not be null")
  private List<OptionDto> options = new ArrayList<>();

}
