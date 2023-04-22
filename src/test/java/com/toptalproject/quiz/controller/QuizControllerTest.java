package com.toptalproject.quiz.controller;

import com.toptalproject.quiz.PostgresqlContainer;
import com.toptalproject.quiz.TokenService;
import com.toptalproject.quiz.dto.OptionDto;
import com.toptalproject.quiz.dto.QuestionDto;
import com.toptalproject.quiz.dto.QuizDto;
import com.toptalproject.quiz.dto.QuizPage;
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
  QuizDto quiz;
  QuizDto quizAttempt;
  QuestionDto newQuestion;

  OptionDto newOptionUnderNewQuestion;

  @BeforeAll
  void setup() throws Exception {
    quizAuthorToken = tokenService.getToken("quizCreatorUser@toptalQuizApp.com", "aA.123456789");
    quizTaker1 = tokenService.getToken("quizTaker01@toptalQuizApp.com", "aA.123456789");
  }


  @Test
  void runApiAutomationTestFlow() {
    createQuiz();
    addQuestion();
    addOption();
    updateOption();
    deleteOption();
    updateQuestion();
    deleteQuestion();
    updateQuiz();
    publishQuestion();
    attemptQuiz();
    loadQuizCreatedByMe();
    loadQuizStat();
    loadQuizzesTakenByQuizTaker();
  }

  private void loadQuizzesTakenByQuizTaker() {
    QuizPage page = sendRequest(null, quizTaker1,
        String.format("attempts?page=0&limit=50", quiz.getId()), QuizPage.class,
        HttpMethod.GET);
    Assertions.assertEquals(1, page.getQuizzes().size());
    QuizDto myStat = page.getQuizzes().get(0);
    Assertions.assertEquals(-0.8333333333333334, myStat.getScore(), 1e-6);
    Assertions.assertEquals(-1.0, myStat.getQuestions().get(0).getScore(), 1e-6);
    Assertions.assertEquals(0.16666666666666663, myStat.getQuestions().get(1).getScore(),
        1e-6);
  }

  private void loadQuizStat() {
    QuizPage page = sendRequest(null, quizAuthorToken,
        String.format("/attempts/stat/%s?page=0&limit=50", quiz.getId()), QuizPage.class,
        HttpMethod.GET);
    Assertions.assertEquals(1, page.getQuizzes().size());
    QuizDto stats = page.getQuizzes().get(0);
    Assertions.assertEquals(-0.8333333333333334, stats.getScore(), 1e-6);
    Assertions.assertEquals(-1.0, stats.getQuestions().get(0).getScore(), 1e-6);
    Assertions.assertEquals(0.16666666666666663, stats.getQuestions().get(1).getScore(),
        1e-6);
  }

  private void loadQuizCreatedByMe() {
    QuizPage page = sendRequest(null, quizAuthorToken, "quizzes?page=0&limit=50", QuizPage.class,
        HttpMethod.GET);
    Assertions.assertEquals(1, page.getQuizzes().size());
  }

  private void attemptQuiz() {
    QuizDto dto = QuizDto.builder()
        .id(quiz.getId())
        .questions(Arrays.asList(
            QuestionDto.builder().
                id(quiz.getQuestions().get(0).getId())
                .options(
                    Arrays.asList(
                        OptionDto.builder()
                            .id(quiz.getQuestions().get(0).getOptions().get(0).getId())
                            .build())).build(),
            QuestionDto.builder().id(quiz.getQuestions().get(1).getId())
                .options(
                    Arrays.asList(
                        OptionDto.builder()
                            .id(quiz.getQuestions().get(1).getOptions().get(0).getId()).build(),
                        OptionDto.builder()
                            .id(quiz.getQuestions().get(1).getOptions().get(1).getId()).build(),
                        OptionDto.builder()
                            .id(quiz.getQuestions().get(1).getOptions().get(2).getId()).build()
                    )).build()

        )).build();

    quizAttempt = sendRequest(dto, quizTaker1, "attempts",
        QuizDto.class, HttpMethod.POST);
    Assertions.assertEquals(-0.8333333333333334, quizAttempt.getScore(), 1e-6);
    Assertions.assertEquals(-1.0, quizAttempt.getQuestions().get(0).getScore(), 1e-6);
    Assertions.assertEquals(0.16666666666666663, quizAttempt.getQuestions().get(1).getScore(),
        1e-6);

  }


  private void publishQuestion() {
    quiz = sendRequest(null, quizAuthorToken, String.format("quizzes/%s/publish", quiz.getId()),
        QuizDto.class, HttpMethod.PUT);
    Assertions.assertEquals(2, quiz.getQuestions().size());
    Assertions.assertEquals(true, quiz.getPublished());
    Assertions.assertEquals(LocalDate.now(), quiz.getPublishedAt().toLocalDate());
  }

  private void updateQuiz() {
    String title = "My first quiz ever!";
    QuizDto quizDto = QuizDto.builder().title(title).build();
    quiz = sendRequest(quizDto, quizAuthorToken, String.format("quizzes/%s", quiz.getId()),
        QuizDto.class, HttpMethod.PUT);
    Assertions.assertEquals(2, quiz.getQuestions().size());
    Assertions.assertEquals(false, quiz.getPublished());
    Assertions.assertEquals(title, quizDto.getTitle());

  }

  private void deleteQuestion() {
    quiz = sendRequest(null, quizAuthorToken,
        String.format("quizzes/%s/questions/%s", quiz.getId(), newQuestion.getId()), QuizDto.class,
        HttpMethod.DELETE);
    newQuestion =
        quiz.getQuestions().stream().filter(q -> newQuestion.getId().equals(q.getId())).findAny()
            .orElse(null);
    Assertions.assertNull(newQuestion);
    Assertions.assertEquals(2, quiz.getQuestions().size());
  }

  private void updateQuestion() {
    String title = "Which is the largest state in the USA";
    QuestionDto questionDto = QuestionDto.builder().text(title).multipleAnswer(false).build();
    quiz = sendRequest(questionDto, quizAuthorToken,
        String.format("quizzes/%s/questions/%s", quiz.getId(), newQuestion.getId()), QuizDto.class,
        HttpMethod.PUT);
    newQuestion =
        quiz.getQuestions().stream().filter(q -> newQuestion.getId().equals(q.getId())).findAny()
            .orElse(null);
    Assertions.assertNotNull(newQuestion);
    Assertions.assertEquals(3, quiz.getQuestions().size());
    Assertions.assertEquals(title, newQuestion.getText());
  }

  private void deleteOption() {
    quiz = sendRequest(null, quizAuthorToken,
        String.format("quizzes/%s/questions/%s/options/%s", quiz.getId(), newQuestion.getId(),
            newOptionUnderNewQuestion.getId()), QuizDto.class, HttpMethod.DELETE);
    newQuestion =
        quiz.getQuestions().stream().filter(q -> newQuestion.getId().equals(q.getId())).findAny()
            .orElse(null);
    Assertions.assertNotNull(newQuestion);
    Assertions.assertEquals(2, newQuestion.getOptions().size());
    newOptionUnderNewQuestion = newQuestion.getOptions().stream()
        .filter(a -> newOptionUnderNewQuestion.getId().equals(a.getId())).findAny().orElse(null);
    Assertions.assertNull(newOptionUnderNewQuestion);
  }

  private void updateOption() {
    String questionText = "California";
    OptionDto optionDto = OptionDto.builder().text(questionText).correct(false).build();

    quiz = sendRequest(optionDto, quizAuthorToken,
        String.format("quizzes/%s/questions/%s/options/%s", quiz.getId(), newQuestion.getId(),
            newOptionUnderNewQuestion.getId()), QuizDto.class, HttpMethod.PUT);
    newQuestion =
        quiz.getQuestions().stream().filter(q -> newQuestion.getId().equals(q.getId())).findAny()
            .orElse(null);
    Assertions.assertNotNull(newQuestion);
    Assertions.assertEquals(3, newQuestion.getOptions().size());
    newOptionUnderNewQuestion =
        newQuestion.getOptions().stream().filter(a -> questionText.equals(a.getText())).findAny()
            .orElse(null);
    Assertions.assertNotNull(newOptionUnderNewQuestion);
  }

  private void addOption() {
    String questionText = "Kalifornia";
    OptionDto optionDto = OptionDto.builder().text(questionText).correct(false).build();

    quiz = sendRequest(optionDto, quizAuthorToken,
        String.format("quizzes/%s/questions/%s/options", quiz.getId(), newQuestion.getId()),
        QuizDto.class, HttpMethod.POST);
    newQuestion =
        quiz.getQuestions().stream().filter(q -> newQuestion.getId().equals(q.getId())).findAny()
            .orElse(null);
    Assertions.assertNotNull(newQuestion);
    Assertions.assertEquals(3, newQuestion.getOptions().size());
    newOptionUnderNewQuestion =
        newQuestion.getOptions().stream().filter(a -> questionText.equals(a.getText())).findAny()
            .orElse(null);
    Assertions.assertNotNull(newOptionUnderNewQuestion);
  }

  private void addQuestion() {
    String title = "Which is the largest state in the United States of America";
    QuestionDto questionDto = QuestionDto.builder().text(title).multipleAnswer(false).options(
        Arrays.asList(OptionDto.builder().text("Texas").correct(false).build(),
            OptionDto.builder().text("Alaska").correct(true).build())).build();
    quiz = sendRequest(questionDto, quizAuthorToken,
        String.format("quizzes/%s/questions", quiz.getId()), QuizDto.class, HttpMethod.POST);
    newQuestion =
        quiz.getQuestions().stream().filter(q -> title.equals(q.getText())).findAny().orElse(null);
    Assertions.assertNotNull(newQuestion);
    Assertions.assertEquals(3, quiz.getQuestions().size());
  }

  private void createQuiz() {
    QuizDto dto = QuizDto.builder().title("My first quiz").published(false).questions(Arrays.asList(
        QuestionDto.builder().text("Moon is a star").multipleAnswer(false).options(
            Arrays.asList(OptionDto.builder().text("Yes").correct(false).build(),
                OptionDto.builder().text("No").correct(true).build())).build(),
        QuestionDto.builder().text("Temperature can be measured in").multipleAnswer(true).options(
            Arrays.asList(
                OptionDto.builder().text("Kelvin").correct(true).build(),
                OptionDto.builder().text("Fahrenheit").correct(true).build(),
                OptionDto.builder().text("Gram").correct(false).build(),
                OptionDto.builder().text("Celsius").correct(true).build(),
                OptionDto.builder().text("Liters").correct(false).build()
            )).build()

    )).build();
    quiz = sendRequest(dto, quizAuthorToken, "quizzes", QuizDto.class, HttpMethod.POST);
    Assertions.assertEquals("My first quiz", quiz.getTitle());
    Assertions.assertEquals(false, quiz.getPublished());
    Assertions.assertEquals(2, quiz.getQuestions().size());

  }

  private <T, S> S sendRequest(T data, String token, String path, Class<S> tClass,
                               HttpMethod method) {
    final String baseUrl = String.format("http://localhost:%s/%s", port, path);
    final HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    final HttpEntity<Object> request = new HttpEntity<>(data, headers);
    ResponseEntity<S> response = this.restTemplate.exchange(baseUrl, method, request, tClass);
    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
    return response.getBody();
  }
}