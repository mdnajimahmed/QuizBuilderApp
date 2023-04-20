package com.toptalproject.quiz.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "questions")
@NoArgsConstructor
@Getter
@Setter
public class Question extends BaseEntity {
  private String text;
  private boolean isMultipleAnswer;
  @ManyToOne
  @JoinColumn(name="quiz_id", nullable=false)
  private Quiz quiz;
  @OneToMany(mappedBy = "question",fetch = FetchType.EAGER)
  private List<Answer> answers;
}
