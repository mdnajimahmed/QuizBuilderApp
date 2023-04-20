package com.toptalproject.quiz.data.entity;

import com.toptalproject.quiz.data.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "quizzes")
@NoArgsConstructor
@Getter
@Setter
public class Quiz extends BaseEntity {
  private String title;
  private Boolean isPublished;
}
