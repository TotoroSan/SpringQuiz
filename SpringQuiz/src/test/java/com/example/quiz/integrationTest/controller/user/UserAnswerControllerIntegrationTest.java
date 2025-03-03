// file: src/test/java/com/example/quiz/controller/user/UserAnswerControllerIntegrationTest.java
package com.example.quiz.integrationTest.controller.user;

import com.example.quiz.model.dto.AnswerDto;
import com.example.quiz.model.entity.*;
import com.example.quiz.service.user.UserAnswerService;
import com.example.quiz.service.user.UserQuizStateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserAnswerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserAnswerService userAnswerService;

    @MockBean
    private UserQuizStateService userQuizStateService;

    // Helper method to simulate authentication
    private RequestPostProcessor getAuthToken(User user) {
        return authentication(new TestingAuthenticationToken(user, null));
    }

    @Test
    public void testSubmitAnswer_NoQuizState() throws Exception {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        AnswerDto answerDto = new AnswerDto(100L, "Sample answer");

        // Simulate no quiz state found for user
        when(userQuizStateService.getLatestQuizStateByUserId(eq(1L)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/user/api/answers/answer")
                        .with(getAuthToken(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSubmitAnswer_Correct() throws Exception {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        // Create a quiz state with a current question having a correct answer.
        QuizState quizState = new QuizState();
        Question currentQuestion = new Question();
        CorrectAnswer correctAnswer = new CorrectAnswer();
        correctAnswer.setId(200L);
        currentQuestion.setCorrectAnswer(correctAnswer);

        // Set up mocks
        when(userQuizStateService.getLatestQuizStateByUserId(eq(1L)))
                .thenReturn(Optional.of(quizState));
        when(userQuizStateService.getCurrentQuestion(eq(quizState)))
                .thenReturn(currentQuestion);
        // if ids match the answer is correct
        AnswerDto answerDto = new AnswerDto(200L, "Correct answer");
        when(userAnswerService.isCorrectAnswer(eq(answerDto), eq(currentQuestion)))
                .thenReturn(Boolean.TRUE);
        // Simulate processing of correct answer as void
        doNothing().when(userQuizStateService).processCorrectAnswerSubmission(eq(quizState));

        mockMvc.perform(post("/user/api/answers/answer")
                        .with(getAuthToken(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void testSubmitAnswer_Incorrect() throws Exception {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        // Create a quiz state with a current question having a correct answer.
        QuizState quizState = new QuizState();
        Question currentQuestion = new Question();
        CorrectAnswer correctAnswer = new CorrectAnswer();
        correctAnswer.setId(300L);
        currentQuestion.setCorrectAnswer(correctAnswer);

        // Set up mocks
        when(userQuizStateService.getLatestQuizStateByUserId(eq(1L)))
                .thenReturn(Optional.of(quizState));
        when(userQuizStateService.getCurrentQuestion(eq(quizState)))
                .thenReturn(currentQuestion);
        // If ids do not match answer is incorrect
        AnswerDto answerDto = new AnswerDto(400L, "Incorrect answer");
        when(userAnswerService.isCorrectAnswer(eq(answerDto), eq(currentQuestion)))
                .thenReturn(Boolean.FALSE);
        doNothing().when(userQuizStateService).processIncorrectAnswerSubmission(eq(quizState));

        mockMvc.perform(post("/user/api/answers/answer")
                        .with(getAuthToken(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}