package com.toptalproject.quiz.service;

import com.toptalproject.quiz.data.entity.Quiz;
import com.toptalproject.quiz.data.repository.QuizRepository;
import com.toptalproject.quiz.service.impl.FormatterImpl;
import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;

import static java.awt.SystemColor.text;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FormatterTest {
    @Mock
    private QuizRepository quizRepository;

    @InjectMocks // in integration test we use @MockBean
    private FormatterImpl formatter; // autowire does not make any difference for injectmocks
    Quiz quiz;
    @BeforeEach
    void setup(){
        Faker faker = new Faker();
        quiz = new Quiz();
        quiz.setTitle(faker.text().text());
        quiz.setPublished(faker.bool().bool());
        quiz.setPublishedAt(LocalDateTime.now());
    }

    @Test
    void format() {

        // given
        BDDMockito.given(quizRepository.findAll()).willReturn(List.of(quiz));
        // when
        String test = formatter.format("test");
        //
        String exp = "TEST<->"+quiz.getTitle().toUpperCase();

        Assertions.assertThat(test).isEqualTo(exp);
    }
}