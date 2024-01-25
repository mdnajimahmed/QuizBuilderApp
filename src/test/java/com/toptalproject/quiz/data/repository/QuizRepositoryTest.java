package com.toptalproject.quiz.data.repository;

import com.toptalproject.quiz.data.entity.Quiz;
import jakarta.persistence.EntityManager;
import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class QuizRepositoryTest {
    Faker faker = new Faker();

    @Container
    static PostgreSQLContainer database = new PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName(UUID.randomUUID().toString())
            .withUsername(UUID.randomUUID().toString())
            .withPassword(UUID.randomUUID().toString());

    @DynamicPropertySource

    static void setDataSourceProperties(DynamicPropertyRegistry propertyRegistry) {
        propertyRegistry.add("spring.datasource.url",database::getJdbcUrl);
        propertyRegistry.add("spring.datasource.username",database::getUsername);
        propertyRegistry.add("spring.datasource.password",database::getPassword);
//        propertyRegistry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:5432/quiz_app");
//        propertyRegistry.add("spring.datasource.username", () -> "postgres");
//        propertyRegistry.add("spring.datasource.password", () -> "postgres");
    }

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private QuizRepository repository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void contextLoads() {
        assertNotNull(entityManager);
        assertNotNull(dataSource);
    }

    @Test
//    @Rollback(false)
    void givenQuizObject_whenSave_thenReturnSavedQuiz() {
        // given
        Quiz quiz = new Quiz();
        quiz.setTitle(faker.text().text());
        quiz.setPublished(faker.bool().bool());
        quiz.setPublishedAt(LocalDateTime.now());

        // when
        Quiz savedQuiz = repository.save(quiz);

        //then
        Assertions.assertThat(savedQuiz).isNotNull();
        Assertions.assertThat(savedQuiz.getId()).isNotNull();


    }

    @Sql({"/quiz-1.sql"})
    @Test
    void findByCreatedBy() {
        Page<Quiz> page = repository.findByCreatedBy("najim.ju@gmail.com", PageRequest.of(0, 10));
        // @Sql({"/quiz-1.sql","/quiz-2.sql"}) fails the test because it expects 2 rows.
        Assertions.assertThat(page.get().count()).isEqualTo(1);
    }

    @Sql({"/quiz-2.sql"})
    @Test
    void givenQuizObject_whenFindById_shouldReturnTheObject() {
        Quiz quiz = repository.findById(UUID.fromString("26ec465e-2445-41ba-aecf-c56c07732865")).orElse(null);
        Assertions.assertThat(quiz).isNotNull();

        Quiz quiz2 = repository.findById(UUID.fromString("26ec465e-2445-41ba-aecf-c56c07732864")).orElse(null);
        Assertions.assertThat(quiz2).isNull();
    }

    @Sql({"/quiz-1.sql"})
    @Test
    @Transactional(propagation = NOT_SUPPORTED)
    void givenQuizObject_whenUpdateTitle_thenTheTitleShouldBeUpdated() {
        int modifiedRows = repository.updateTitleCustomQuery(UUID.fromString("26ec465e-2445-41ba-aecf-c56c07732864"),"The test title");
        Assertions.assertThat(modifiedRows).isEqualTo(1);
        Quiz quiz = repository.findById(UUID.fromString("26ec465e-2445-41ba-aecf-c56c07732864")).orElse(null);
        Assertions.assertThat(quiz).isNotNull();
        Assertions.assertThat(quiz.getTitle()).isEqualTo("The test title");
    }

    @Sql({"/quiz-1.sql"})
    @Test
    void givenQuizObject_whenUpdateTitle_thenTheTitleShouldBeUpdatedAndPCRefreshed() {
        Quiz quiz = repository.findById(UUID.fromString("26ec465e-2445-41ba-aecf-c56c07732864")).orElse(null);
        Assertions.assertThat(quiz).isNotNull();
        quiz.setTitle("The test title");
        Quiz updatedQuiz = repository.save(quiz);
        Assertions.assertThat(updatedQuiz).isNotNull();
        Assertions.assertThat(updatedQuiz.getTitle()).isEqualTo("The test title");
        // save method returns the same object!
        Assertions.assertThat(quiz).isSameAs(updatedQuiz);
    }

    @Sql({"/quiz-1.sql"})
    @Test
    void givenQuizObject_whenDeleteIt_thenQuizShouldBeDeleted() {
        Quiz quiz = repository.findById(UUID.fromString("26ec465e-2445-41ba-aecf-c56c07732864")).orElse(null);
        Assertions.assertThat(quiz).isNotNull();
        repository.delete(quiz);

        Optional<Quiz> deletedQuiz = repository.findById(UUID.fromString("26ec465e-2445-41ba-aecf-c56c07732864"));
        Assertions.assertThat(deletedQuiz).isEmpty();

    }

    @Test
    @DisplayName("findAvailableQuizzesToTake")
    void findAvailableQuizzesToTake() {
//        System.out.println("database =>" + database);
    }

    @Test
    void countAvailableQuizzesToTake() {
//        System.out.println("database =>" + database);
    }
}