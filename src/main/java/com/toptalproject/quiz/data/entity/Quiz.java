package com.toptalproject.quiz.data.entity;

import com.toptalproject.quiz.data.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "quizzes")
@NoArgsConstructor
@Getter
@Setter
public class Quiz extends BaseEntity {
  private String title;
  private boolean published;
  @Column(name = "published_at")
  @CreatedDate
  private LocalDateTime publishedAt;
  @OneToMany(mappedBy = "quiz",fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST,CascadeType.REMOVE},orphanRemoval = true)
  private List<Question> questions = new ArrayList<>();
  public void addQuestion(Question question){
    this.questions.add(question);
    question.setQuiz(this);
  }

  public void removeQuestion(Question question) {
    this.questions.remove(question);
    question.setQuiz(null);
  }
}
