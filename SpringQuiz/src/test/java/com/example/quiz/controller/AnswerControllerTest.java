package com.example.quiz.controller;

import com.example.quiz.controller.admin.AdminAnswerController;
import com.example.quiz.model.Answer;
import com.example.quiz.model.CorrectAnswer;
import com.example.quiz.service.admin.AdminAnswerService;

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

@WebMvcTest(AdminAnswerController.class)
public class AnswerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminAnswerService adminAnswerService;
   // TODO IMPLEMENT
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetAnswerById() throws Exception {
        // Arrange
    	CorrectAnswer answer = new CorrectAnswer();
        answer.setAnswerText("Sample Answer");
        when(adminAnswerService.getAnswerById(1L)).thenReturn(Optional.of(answer));

        // Act & Assert
        mockMvc.perform(get("/api/answers/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answerText").value("Sample Answer"));

        verify(adminAnswerService, times(1)).getAnswerById(1L);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreateAnswer() throws Exception {
        // Arrange
    	CorrectAnswer answer = new CorrectAnswer();
        answer.setAnswerText("New Answer");
        when(adminAnswerService.createAnswer(any(CorrectAnswer.class))).thenReturn(answer);

        // Act & Assert
        mockMvc.perform(post("/api/answers")
        		.with(csrf())  // Add CSRF toke
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"answerText\": \"New Answer\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answerText").value("New Answer"));

        verify(adminAnswerService, times(1)).createAnswer(any(CorrectAnswer.class));
    }
}
