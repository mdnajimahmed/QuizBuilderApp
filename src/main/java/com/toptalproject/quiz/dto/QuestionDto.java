package com.toptalproject.quiz.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionDto {
  private UUID id;
  @NotEmpty(message = "Question text can not be empty")
  private String text;
  private Boolean multipleAnswer;
  private Double score;
  @Size(min = 1,max = 5, message = "The question is allowed to have at least 1 and at most 5 options")
  private List<OptionDto> options = new ArrayList<>();

}
