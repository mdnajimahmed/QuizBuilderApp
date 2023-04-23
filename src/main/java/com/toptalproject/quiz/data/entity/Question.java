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
@Table(name = "questions")
@NoArgsConstructor
@Getter
@Setter
public class Question extends BaseEntity {
  private String text;
  @ManyToOne
  @JoinColumn(name = "quiz_id", nullable = false)
  private Quiz quiz;
  @OneToMany(mappedBy = "question", fetch = FetchType.EAGER,
      cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
  private List<Option> options = new ArrayList<>();

  public void addOption(Option option) {
    this.options.add(option);
    option.setQuestion(this);
  }

  public void removeOption(Option option) {
    this.options.remove(option);
    option.setQuestion(null);
  }
}
