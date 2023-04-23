package com.toptalproject.quiz.data.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "quiz_attempts")
@NoArgsConstructor
@Getter
@Setter
public class QuizAttempt extends BaseEntity {
  @ManyToOne
  @JoinColumn(name = "quiz_id", referencedColumnName = "id", nullable = false)
  private Quiz quiz;
  private double score;
  @OneToMany(mappedBy = "quizAttempt", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST,
      CascadeType.REMOVE})
  private List<QuestionAttempt> questionAttempts = new ArrayList<>();

  public void addQuestionAttempt(final QuestionAttempt questionAttempt) {
    this.questionAttempts.add(questionAttempt);
    questionAttempt.setQuizAttempt(this);
  }
}
