package com.toptalproject.quiz.data.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "question_attempts")
@NoArgsConstructor
@Getter
@Setter
public class QuestionAttempt extends BaseEntity {
  private double score;
  private boolean skipped;
  private String selectedAnswerIds;
  @ManyToOne
  @JoinColumn(name="quiz_attempt_id", nullable=false)
  private QuizAttempt quizAttempt;
  @OneToOne
  @JoinColumn(name = "question_id", referencedColumnName = "id")
  private Question question;
}
