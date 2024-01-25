package com.toptalproject.quiz.service.impl;

import com.toptalproject.quiz.data.entity.Quiz;
import com.toptalproject.quiz.data.repository.QuizRepository;
import com.toptalproject.quiz.service.Formatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FormatterImpl implements Formatter {
    @Autowired
    private QuizRepository quizRepository;

    @Override
    public String format(String text) {
        String randomQuizTitle = quizRepository.findAll().stream().findAny().map(Quiz::getTitle).orElse("missing");
        return (text + "<->" + randomQuizTitle).toUpperCase();
    }
}
