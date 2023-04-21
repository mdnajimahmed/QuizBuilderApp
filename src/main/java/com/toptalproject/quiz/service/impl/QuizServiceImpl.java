package com.toptalproject.quiz.service.impl;

import com.toptalproject.quiz.data.entity.Answer;
import com.toptalproject.quiz.data.entity.Question;
import com.toptalproject.quiz.dto.request.AnswerRequest;
import com.toptalproject.quiz.dto.request.QuestionRequest;
import com.toptalproject.quiz.error.NotFoundException;
import com.toptalproject.quiz.data.entity.Quiz;
import com.toptalproject.quiz.data.repository.QuizRepository;
import com.toptalproject.quiz.dto.request.QuizRequest;
import com.toptalproject.quiz.service.QuizService;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class QuizServiceImpl implements QuizService {
  private final QuizRepository quizRepository;

  QuizServiceImpl(QuizRepository quizRepository) {
    this.quizRepository = quizRepository;
  }

  @Override
  public void createQuiz(QuizRequest request) {
    Quiz quiz = new Quiz();
    quiz.setTitle(request.getTitle());
    quiz.setIsPublished(request.isPublished());
    request.getQuestions().forEach(q -> quiz.addQuestion(mapToQuestion(q)));
    quizRepository.save(quiz);
  }

  private Question mapToQuestion(QuestionRequest questionRequest) {
    Question question = new Question();
    question.setText(questionRequest.getText());
    question.setMultipleAnswer(questionRequest.isMultipleAnswer());
    questionRequest.getAnswers()
        .forEach(answerRequest -> question.addAnswer(mapToAnswer(answerRequest)));
    return question;
  }
  private Answer mapToAnswer(AnswerRequest answerRequest) {
    Answer answer = new Answer();
    answer.setText(answerRequest.getText());
    answer.setCorrect(answerRequest.isCorrect());
    return answer;
  }

  @Override
  @Transactional
  public void updateQuiz(UUID id, QuizRequest request) {
    Quiz quiz = quizRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(Quiz.class.getCanonicalName(), id));
    quiz.setTitle(request.getTitle());
    quiz.setIsPublished(request.isPublished());
  }
}
