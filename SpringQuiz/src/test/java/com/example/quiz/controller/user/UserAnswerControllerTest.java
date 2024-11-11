package com.example.quiz.controller.user;

import com.example.quiz.model.dto.AnswerDto;
import com.example.quiz.model.entity.Question;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.model.entity.User;
import com.example.quiz.service.user.UserAnswerService;
import com.example.quiz.service.user.UserQuizStateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import jakarta.servlet.http.HttpSession;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserAnswerControllerTest {

    @Mock
    private UserAnswerService userAnswerService;

    @Mock
    private UserQuizStateService userQuizStateService;

    @Mock
    private HttpSession session;

    @Mock
    private User user;

    @InjectMocks
    private UserAnswerController userAnswerController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSubmitAnswer_CorrectAnswer() {
        // Arrange
        AnswerDto answerDto = new AnswerDto();
        QuizState quizState = new QuizState(1L);
        Question currentQuestion = new Question();
        currentQuestion.setId(1L);

        when(user.getId()).thenReturn(1L);
        when(userQuizStateService.getLatestQuizStateByUserId(1L)).thenReturn(Optional.of(quizState));
        when(userQuizStateService.getCurrentQuestion(quizState)).thenReturn(currentQuestion);
        when(userAnswerService.isCorrectAnswer(answerDto, currentQuestion)).thenReturn(true);

        // Act
        ResponseEntity<Boolean> response = userAnswerController.submitAnswer(answerDto, user, session);

        // Assert
        assertEquals(ResponseEntity.ok(true), response);
        verify(userQuizStateService, times(1)).markQuestionAsCompleted(quizState, currentQuestion.getId());
        verify(userQuizStateService, times(1)).incrementScore(quizState);
        verify(userQuizStateService, times(1)).incrementCurrentRound(quizState);
        verify(userQuizStateService, times(1)).saveQuizState(quizState);
        verify(session, times(1)).setAttribute("quizState", quizState);
    }

    @Test
    public void testSubmitAnswer_WrongAnswer() {
        // Arrange
        AnswerDto answerDto = new AnswerDto();
        QuizState quizState = new QuizState(1L);
        Question currentQuestion = new Question();
        currentQuestion.setId(1L);

        when(user.getId()).thenReturn(1L);
        when(userQuizStateService.getLatestQuizStateByUserId(1L)).thenReturn(Optional.of(quizState));
        when(userQuizStateService.getCurrentQuestion(quizState)).thenReturn(currentQuestion);
        when(userAnswerService.isCorrectAnswer(answerDto, currentQuestion)).thenReturn(false);

        // Act
        ResponseEntity<Boolean> response = userAnswerController.submitAnswer(answerDto, user, session);

        // Assert
        assertEquals(ResponseEntity.ok(false), response);
        verify(userQuizStateService, never()).markQuestionAsCompleted(any(), any());
        verify(userQuizStateService, never()).incrementScore(any());
        verify(userQuizStateService, never()).incrementCurrentRound(any());
        verify(userQuizStateService, never()).saveQuizState(any());
        verify(session, never()).setAttribute(eq("quizState"), any());
    }

    @Test
    public void testSubmitAnswer_QuizStateNotFound() {
        // Arrange
        AnswerDto answerDto = new AnswerDto();
        when(user.getId()).thenReturn(1L);
        when(userQuizStateService.getLatestQuizStateByUserId(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Boolean> response = userAnswerController.submitAnswer(answerDto, user, session);

        // Assert
        assertEquals(ResponseEntity.badRequest().body(null), response);
    }
} 
