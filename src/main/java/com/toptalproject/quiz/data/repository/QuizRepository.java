package com.toptalproject.quiz.data.repository;

import com.toptalproject.quiz.data.entity.Quiz;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuizRepository extends JpaRepository<Quiz, UUID> {

  Page<Quiz> findByCreatedBy(String currentUser, PageRequest pageRequest);
  @Query(nativeQuery = true, value = "select * from quizzes q " +
      "where q.created_by!=:currentUser AND q.published=true " +
      "AND not exists " +
      "(select 1 from quiz_attempts qa where qa.quiz_id=q.id AND qa.created_by=:currentUser) " +
      "order by q.published_at desc " +
      "OFFSET :skip ROWS " +
      "FETCH FIRST :limit ROW ONLY;"
  )
  List<Quiz> findAvailableQuizzesToTake(String currentUser, int skip, int limit);

  @Query(value = "select count(*) from quizzes q " +
      "where q.created_by!=:currentUser AND q.published=true " +
      "AND not exists" +
      "(select 1 from quiz_attempts qa where qa.quiz_id=q.id AND qa.created_by=:currentUser)",
      nativeQuery = true)
  int countAvailableQuizzesToTake(String currentUser);

}
