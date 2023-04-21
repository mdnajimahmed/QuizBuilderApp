//package com.toptalproject.quiz.data.entity;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.OneToOne;
//import jakarta.persistence.Table;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@Entity
//@Table(name = "answer_attempts")
//@NoArgsConstructor
//@Getter
//@Setter
//public class AnswerAttempt extends BaseEntity {
//  private boolean selected;
//  @ManyToOne
//  @JoinColumn(name="question_attempt_id", nullable=false)
//  private QuestionAttempt questionAttempt;
//  @OneToOne
//  @JoinColumn(name = "answer_id", referencedColumnName = "id")
//  private Answer answer;
//}
