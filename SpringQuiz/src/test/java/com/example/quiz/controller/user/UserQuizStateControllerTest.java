package com.example.quiz.controller.user;

import com.example.quiz.model.dto.QuizStateDto;
import com.example.quiz.model.entity.Question;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.model.entity.User;
import com.example.quiz.service.user.UserQuizStateService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserQuizStateControllerTest {

    @Mock
    private UserQuizStateService userQuizStateService;

    @Mock
    private HttpSession session;

    @Mock
    private User user;

    @InjectMocks
    private UserQuizStateController userQuizStateController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testStartQuiz_Success() {
        // Arrange
        Long userId = 1L;
        QuizState quizState = new QuizState(userId);

        when(user.getId()).thenReturn(userId);
        when(userQuizStateService.startNewQuiz(userId)).thenReturn(quizState);

        // Act
        ResponseEntity<String> response = userQuizStateController.startQuiz(session, user);

        // Assert
        assertEquals(ResponseEntity.ok("Quiz started!"), response);
        verify(userQuizStateService, times(1)).startNewQuiz(userId);
        verify(session, times(1)).setAttribute("quizState", quizState);
    }

    @Test
    public void testGetQuizState_Success() {
        // Arrange
        Long userId = 1L;
        QuizState quizState = new QuizState(userId);
        quizState.setCurrentQuestionIndex(0);
        quizState.getAllQuestions().add(new Question("Sample Question"));

        when(user.getId()).thenReturn(userId);
        when(userQuizStateService.getLatestQuizStateByUserId(userId)).thenReturn(Optional.of(quizState));

        // Act
        ResponseEntity<QuizStateDto> response = userQuizStateController.getQuizState(session, user);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(quizState.getScore(), response.getBody().getScore());
        assertEquals(quizState.getCurrentRound(), response.getBody().getCurrentRound());
        assertEquals(quizState.getAllQuestions().get(0).getQuestionText(), response.getBody().getCurrentQuestionText());
        verify(session, times(1)).setAttribute("quizState", quizState);
    }

    @Test
    public void testGetQuizState_QuizStateNotFound() {
        // Arrange
        Long userId = 1L;

        when(user.getId()).thenReturn(userId);
        when(userQuizStateService.getLatestQuizStateByUserId(userId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<QuizStateDto> response = userQuizStateController.getQuizState(session, user);

        // Assert
        assertEquals(ResponseEntity.badRequest().build(), response);
        verify(userQuizStateService, times(1)).getLatestQuizStateByUserId(userId);
        verify(session, never()).setAttribute(anyString(), any());
    }
}
