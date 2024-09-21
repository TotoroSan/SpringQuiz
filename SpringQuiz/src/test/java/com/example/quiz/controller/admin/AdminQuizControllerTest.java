package com.example.quiz.controller.admin;

import com.example.quiz.controller.admin.AdminQuizController;
import com.example.quiz.model.Quiz;
import com.example.quiz.service.admin.AdminQuizService;

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

@WebMvcTest(AdminQuizController.class)
public class AdminQuizControllerTest {
	
	// MockMvc is used to simulate HTTP requests to the controller
    @Autowired
    private MockMvc mockMvc;
    
    // @MockBean is used to mock the service layer that the controller depends on. 
    // This ensures that the controller interacts with the service as expected, 
    // but without relying on the actual business logic in the QuizService.
    @MockBean
    private AdminQuizService adminQuizService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetAllQuizzes() throws Exception {
        // Arrange
        List<Quiz> quizzes = Arrays.asList(new Quiz("Quiz 1"), new Quiz("Quiz 2"));
        when(adminQuizService.getAllQuizzes()).thenReturn(quizzes);

        // Act & Assert
        mockMvc.perform(get("/api/quizzes")
        		.with(csrf())  // Add CSRF toke
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Quiz 1"))
                .andExpect(jsonPath("$[1].title").value("Quiz 2"));

        verify(adminQuizService, times(1)).getAllQuizzes();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreateQuiz() throws Exception {
        // Arrange
        Quiz quiz = new Quiz();
        quiz.setTitle("New Quiz");
        when(adminQuizService.createQuiz(any(Quiz.class))).thenReturn(quiz);

        // Act & Assert
        mockMvc.perform(post("/api/quizzes")
        		.with(csrf())  // Add CSRF toke
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"New Quiz\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Quiz"));

        verify(adminQuizService, times(1)).createQuiz(any(Quiz.class));
    }
}
