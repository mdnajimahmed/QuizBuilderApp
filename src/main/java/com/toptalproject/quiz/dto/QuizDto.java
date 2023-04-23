package com.toptalproject.quiz.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizDto {
  private UUID id;
  @NotEmpty(message = "quiz title can not be empty")
  @Length(max = 255, message = "Maximum 255 character is allowed in quiz title")
  private String title;
  private Boolean published;
  private LocalDateTime publishedAt;
  private Double score;
  private String attemptedBy;
  @Size(min = 1, max = 10, message = "The quiz is allowed to have 1 to 10 questions")
  List<@Valid QuestionDto> questions = new ArrayList<>();
}
