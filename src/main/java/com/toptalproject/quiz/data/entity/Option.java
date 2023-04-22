package com.toptalproject.quiz.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "options")
@NoArgsConstructor
@Getter
@Setter
public class Option extends BaseEntity {
  private String text;
  private boolean isCorrect;
  @ManyToOne
  @JoinColumn(name="question_id", nullable=false)
  private Question question;
}
