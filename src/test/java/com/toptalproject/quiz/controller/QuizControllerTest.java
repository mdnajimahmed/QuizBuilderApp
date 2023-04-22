package com.toptalproject.quiz.controller;

import com.toptalproject.quiz.PostgresqlContainer;
import com.toptalproject.quiz.TokenService;
import com.toptalproject.quiz.dto.AnswerDto;
import com.toptalproject.quiz.dto.QuestionDto;
import com.toptalproject.quiz.dto.QuizDto;
import java.time.LocalDate;
import java.util.Arrays;
import org.junit.ClassRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuizControllerTest {
  @ClassRule
  public static PostgreSQLContainer postgreSQLContainer = PostgresqlContainer.getInstance();
  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;
  @Autowired
  private TokenService tokenService;
  private String quizAuthorToken;
  private String quizTaker1;
  private String quizTaker2;
  QuizDto quiz;
  QuestionDto newQuestion;

  AnswerDto newAnswerUnderNewQuestion;

  @BeforeAll
  void setup() throws Exception {
    quizAuthorToken = tokenService.getToken("quizCreatorUser@toptalQuizApp.com", "aA.123456789");
    quizTaker1 = tokenService.getToken("quizTaker01@toptalQuizApp.com", "aA.123456789");
    quizTaker2 = tokenService.getToken("quizTaker02@toptalQuizApp.com", "aA.123456789");
  }


  @Test
  void runApiAutomationTestFlow() {
    createQuiz();
    addQuestion();
    addAnswer();
    updateAnswer();
    deleteAnswer();
    updateQuestion();
    deleteQuestion();
    updateQuiz();
    publishQuestion();
//    attemptQuiz(quizTaker1);
//    attemptQuiz(quizTaker2);
//    loadQuizCreateByMe();
//    loadQuizStat();
//    loadQuizzesTakenBy(quizTaker1);
//    loadQuizzesTakenBy(quizTaker2);
  }

  private void publishQuestion() {
    quiz = sendRequest(null, quizAuthorToken,
        String.format("quizzes/%s/publish", quiz.getId()),
        QuizDto.class, HttpMethod.PUT);
    Assertions.assertTrue(quiz.getQuestions().size() == 1);
    Assertions.assertEquals(true,quiz.getPublished());
    Assertions.assertEquals(LocalDate.now(),quiz.getPublishedAt().toLocalDate());
  }

  private void updateQuiz() {
    String title = "My first quiz ever!";
    QuizDto quizDto = QuizDto.builder().title(title).build();
    quiz = sendRequest(quizDto, quizAuthorToken,
        String.format("quizzes/%s", quiz.getId()),
        QuizDto.class, HttpMethod.PUT);
    Assertions.assertTrue(quiz.getQuestions().size() == 1);
    Assertions.assertEquals(false,quiz.getPublished());
    Assertions.assertEquals(title,quizDto.getTitle());

  }

  private void deleteQuestion() {
    quiz = sendRequest(null, quizAuthorToken,
        String.format("quizzes/%s/questions/%s", quiz.getId(), newQuestion.getId()),
        QuizDto.class, HttpMethod.DELETE);
    newQuestion =
        quiz.getQuestions().stream().filter(q -> newQuestion.getId().equals(q.getId())).findAny()
            .orElse(null);
    Assertions.assertNull(newQuestion);
    Assertions.assertTrue(quiz.getQuestions().size() == 1);
  }

  private void updateQuestion() {
    String title = "Which is the largest state in the USA";
    QuestionDto questionDto = QuestionDto.builder().text(title)
        .multipleAnswer(false)
        .build();
    quiz = sendRequest(questionDto, quizAuthorToken,
        String.format("quizzes/%s/questions/%s", quiz.getId(), newQuestion.getId()),
        QuizDto.class, HttpMethod.PUT);
    newQuestion =
        quiz.getQuestions().stream().filter(q -> newQuestion.getId().equals(q.getId())).findAny()
            .orElse(null);
    Assertions.assertNotNull(newQuestion);
    Assertions.assertTrue(quiz.getQuestions().size() == 2);
    Assertions.assertEquals(title, newQuestion.getText());
  }

  private void deleteAnswer() {
    quiz = sendRequest(null, quizAuthorToken,
        String.format("quizzes/%s/questions/%s/answers/%s", quiz.getId(), newQuestion.getId(),
            newAnswerUnderNewQuestion.getId()),
        QuizDto.class, HttpMethod.DELETE);
    newQuestion =
        quiz.getQuestions().stream().filter(q -> newQuestion.getId().equals(q.getId())).findAny()
            .orElse(null);
    Assertions.assertNotNull(newQuestion);
    Assertions.assertTrue(newQuestion.getAnswers().size() == 2);
    newAnswerUnderNewQuestion = newQuestion.getAnswers().stream()
        .filter(a -> newAnswerUnderNewQuestion.getId().equals(a.getId())).findAny().orElse(null);
    Assertions.assertNull(newAnswerUnderNewQuestion);
  }

  private void updateAnswer() {
    String questionText = "California";
    AnswerDto answerDto = AnswerDto.builder()
        .text(questionText)
        .correct(false)
        .build();

    quiz = sendRequest(answerDto, quizAuthorToken,
        String.format("quizzes/%s/questions/%s/answers/%s", quiz.getId(), newQuestion.getId(),
            newAnswerUnderNewQuestion.getId()),
        QuizDto.class, HttpMethod.PUT);
    newQuestion =
        quiz.getQuestions().stream().filter(q -> newQuestion.getId().equals(q.getId())).findAny()
            .orElse(null);
    Assertions.assertNotNull(newQuestion);
    Assertions.assertTrue(newQuestion.getAnswers().size() == 3);
    newAnswerUnderNewQuestion = newQuestion.getAnswers().stream()
        .filter(a -> questionText.equals(a.getText())).findAny().orElse(null);
    Assertions.assertNotNull(newAnswerUnderNewQuestion);
  }

  private void addAnswer() {
    String questionText = "Kalifornia";
    AnswerDto answerDto = AnswerDto.builder()
        .text(questionText)
        .correct(false)
        .build();

    quiz = sendRequest(answerDto, quizAuthorToken,
        String.format("quizzes/%s/questions/%s/answers", quiz.getId(), newQuestion.getId()),
        QuizDto.class, HttpMethod.POST);
    newQuestion =
        quiz.getQuestions().stream().filter(q -> newQuestion.getId().equals(q.getId())).findAny()
            .orElse(null);
    Assertions.assertNotNull(newQuestion);
    Assertions.assertTrue(newQuestion.getAnswers().size() == 3);
    newAnswerUnderNewQuestion = newQuestion.getAnswers().stream()
        .filter(a -> questionText.equals(a.getText())).findAny().orElse(null);
    Assertions.assertNotNull(newAnswerUnderNewQuestion);
  }

  private void addQuestion() {
    String title = "Which is the largest state in the United States of America";
    QuestionDto questionDto = QuestionDto.builder().text(title)
        .multipleAnswer(false)
        .answers(Arrays.asList(
            AnswerDto.builder()
                .text("Texas")
                .correct(false)
                .build(),
            AnswerDto.builder()
                .text("Alaska")
                .correct(true)
                .build())).build();
    quiz = sendRequest(questionDto, quizAuthorToken,
        String.format("quizzes/%s/questions", quiz.getId()),
        QuizDto.class, HttpMethod.POST);
    newQuestion =
        quiz.getQuestions().stream().filter(q -> title.equals(q.getText())).findAny().orElse(null);
    Assertions.assertNotNull(newQuestion);
    Assertions.assertTrue(quiz.getQuestions().size() == 2);
  }

  private void createQuiz() {
    QuizDto dto = QuizDto.builder().title("My first quiz").published(false).questions(
        Arrays.asList(
            QuestionDto.builder().text("Moon is a star").multipleAnswer(false)
                .answers(Arrays.asList(
                    AnswerDto.builder()
                        .text("Yes")
                        .correct(false)
                        .build(),
                    AnswerDto.builder()
                        .text("No")
                        .correct(true)
                        .build()
                ))
                .build())).build();
    quiz = sendRequest(dto, quizAuthorToken, "quizzes", QuizDto.class, HttpMethod.POST);
    Assertions.assertEquals("My first quiz", quiz.getTitle());
    Assertions.assertEquals(false, quiz.getPublished());
    Assertions.assertTrue(quiz.getQuestions().size() == 1);

  }

  private <T, S> S sendRequest(T data, String token, String path, Class<S> tClass,
                               HttpMethod method) {
    final String baseUrl = String.format("http://localhost:%s/%s", port, path);
    final HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    final HttpEntity<Object> request = new HttpEntity<>(data, headers);
    ResponseEntity<S> response =
        this.restTemplate.exchange(baseUrl, method, request, tClass);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
    return response.getBody();
  }
}