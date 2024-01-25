package com.toptalproject.quiz.service;

import com.toptalproject.quiz.data.entity.Quiz;
import com.toptalproject.quiz.data.repository.QuizAttemptRepository;
import com.toptalproject.quiz.data.repository.QuizRepository;
import com.toptalproject.quiz.dto.QuizDto;
import com.toptalproject.quiz.service.impl.QuizServiceImpl;
import jakarta.validation.ConstraintViolationException;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.AuditorAware;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {
    @Mock
    private AuditorAware<String> principal;

    @Mock
    private QuizAttemptRepository quizAttemptRepository;
    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private QuizServiceImpl quizService;

    Quiz quiz;

    @BeforeEach
    void setup() {
        quiz = buildNewQuiz();
    }

    private Quiz buildNewQuiz() {
        Faker faker = new Faker();
        Quiz quiz = new Quiz();
        quiz.setTitle(faker.text().text());
        quiz.setPublished(faker.bool().bool());
        quiz.setPublishedAt(LocalDateTime.now());
        return quiz;
    }

    private Quiz buildExistingQuiz(UUID id, String createdBy) {
        Faker faker = new Faker();
        Quiz quiz = new Quiz();
        quiz.setId(id);
        quiz.setTitle(faker.text().text());
        quiz.setPublished(faker.bool().bool());
        quiz.setPublishedAt(LocalDateTime.now());
        quiz.setCreatedBy(createdBy);
        return quiz;
    }


    @AfterEach
    void tearDown() {
    }

    @Test
    void givenTheTitleExists_WhenSaveWithDuplicateTitle_ShouldThrowException() {
        BDDMockito.given(quizRepository.save(any(Quiz.class))).willThrow(ConstraintViolationException.class);
        Assertions.assertThrows(ConstraintViolationException.class,
                () -> {
                    QuizDto quizDto = new QuizDto();
                    quizDto.setPublished(false);
                    quizService.createQuiz(quizDto);
                });
//        verify(quizRepository, never()).save(any(Quiz.class));
    }

    @Test
    void updateQuiz() {
    }

    @Test
    void publishQuiz() {
    }

    @Test
    void addQuestion() {
    }

    @Test
    void updateQuestion() {
    }

    @Test
    void deleteQuestion() {
    }

    @Test
    void addOptionToQuestion() {
    }

    @Test
    void updateOption() {
    }

    @Test
    void deleteOption() {
    }

    @Test
    void getQuizById() {
    }

    @Test
    void getQuizzesAuthoredByMe() {
    }

    @Test
    void deleteQuizById() {
        UUID id = UUID.randomUUID();
        Quiz q = buildExistingQuiz(id, "najim.ju@gmail.com");
        BDDMockito.given(quizRepository.findById(id)).willReturn(Optional.of(q));
        BDDMockito.given(principal.getCurrentAuditor()).willReturn(Optional.of("najim.ju@gmail.com"));
        BDDMockito.given(quizAttemptRepository.existsByQuiz(q)).willReturn(false);
        willDoNothing().given(quizRepository).delete(q);

        quizService.deleteQuizById(id);
        verify(quizRepository, times(1)).delete(q);
    }

    @Test
    void getAvailableQuizzesToTake() {
    }
}