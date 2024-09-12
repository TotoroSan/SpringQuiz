package com.example.quiz.exception;

import com.example.quiz.controller.QuizController;
import com.example.quiz.service.QuizService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuizController.class)
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuizService quizService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testResourceNotFoundException() throws Exception {
        // Arrange: Mock the service to throw the ResourceNotFoundException
        when(quizService.getQuizById(anyLong())).thenThrow(new ResourceNotFoundException("Quiz not found"));

        // Act & Assert
        mockMvc.perform(get("/api/quizzes/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Quiz not found"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testValidationException() throws Exception {
        // Simulate a validation failure on the input
        mockMvc.perform(post("/api/quizzes")
        		.with(csrf())  // Add CSRF toke
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"\"}"))  // Empty title to trigger validation failure
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("must not be empty"));
    }
}
