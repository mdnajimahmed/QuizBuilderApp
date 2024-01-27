package com.toptalproject.quiz.controller;

import com.toptalproject.quiz.ApplicationNoSecurity;
import com.toptalproject.quiz.ControllerAdvisor;
import com.toptalproject.quiz.PrintRequestAndResponseResultHandler;
import com.toptalproject.quiz.config.HttpAuditConfig;
import com.toptalproject.quiz.dto.QuizDto;
import com.toptalproject.quiz.service.impl.QuizServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.wavefront.WavefrontProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(value = QuizController.class)
@ContextConfiguration(classes = {
        QuizController.class, WavefrontProperties.Application.class, ApplicationNoSecurity.class,
        HttpAuditConfig.class, ControllerAdvisor.class
})
class QuizControllerTest {
    @Autowired
    private WebApplicationContext context;

    @MockBean
    private QuizServiceImpl quizService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();


    @Test
    void createQuiz() throws Exception {
        QuizDto dto = new QuizDto();
        BDDMockito.given(quizService.createQuiz(any(QuizDto.class)))
                .willAnswer((invocation -> invocation.getArgument(0)));
        ResultActions response = mockMvc.perform(post("/quizzes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto))
        );
        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(PrintRequestAndResponseResultHandler.printRequestAndResponse())
                .andExpect(jsonPath("$.violations").exists())
                .andExpect(jsonPath("$.violations[0].fieldName").value("title"))
                .andExpect(jsonPath("$.violations[0].message").value("quiz title can not be empty"));


    }
}