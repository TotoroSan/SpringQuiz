package com.example.quiz.controller;

import com.example.quiz.controller.admin.AdminQuizSubmissionController;
import com.example.quiz.model.QuizSubmission;
import com.example.quiz.service.admin.AdminQuizSubmissionService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminQuizSubmissionController.class)
public class QuizSubmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminQuizSubmissionService adminQuizSubmissionService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testSubmitQuiz() throws Exception {
        // Arrange
        QuizSubmission submission = new QuizSubmission();
        submission.setScore(90);
        when(adminQuizSubmissionService.submitQuiz(any(QuizSubmission.class))).thenReturn(submission);

        // Act & Assert
        mockMvc.perform(post("/api/submissions")
        		.with(csrf())  // Add CSRF toke
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"score\": 90}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(90));

        verify(adminQuizSubmissionService, times(1)).submitQuiz(any(QuizSubmission.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetSubmissionsByUserId() throws Exception {
        // Arrange
        List<QuizSubmission> submissions = Arrays.asList(new QuizSubmission(), new QuizSubmission());
        when(adminQuizSubmissionService.getSubmissionsByUserId(1L)).thenReturn(submissions);

        // Act & Assert
        mockMvc.perform(get("/api/submissions/user/1")
        		.with(csrf())  // Add CSRF toke
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(adminQuizSubmissionService, times(1)).getSubmissionsByUserId(1L);
    }
}
