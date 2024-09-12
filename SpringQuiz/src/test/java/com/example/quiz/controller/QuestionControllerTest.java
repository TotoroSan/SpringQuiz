package com.example.quiz.controller;

import com.example.quiz.model.Question;
import com.example.quiz.service.QuestionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuestionController.class)
public class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuestionService questionService;
    // TODO IMPLEMENT
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetQuestionById() throws Exception {
        // Arrange
        Question question = new Question();
        question.setQuestionText("Sample Question");
        when(questionService.getQuestionById(1L)).thenReturn(Optional.of(question));

        // Act & Assert
        mockMvc.perform(get("/api/questions/1")
        		.with(csrf())  // Add CSRF toke
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionText").value("Sample Question"));

        verify(questionService, times(1)).getQuestionById(1L);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreateQuestion() throws Exception {
        // Arrange
        Question question = new Question();
        question.setQuestionText("New Question");
        when(questionService.createQuestion(any(Question.class))).thenReturn(question);

        // Act & Assert
        mockMvc.perform(post("/api/questions")
        		.with(csrf())  // Add CSRF toke
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"questionText\": \"New Question\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionText").value("New Question"));

        verify(questionService, times(1)).createQuestion(any(Question.class));
    }
}
