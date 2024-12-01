package com.example.quiz.controller.user;

import com.example.quiz.model.dto.GameEventDto;
import com.example.quiz.model.dto.QuestionGameEventDto;
import com.example.quiz.model.entity.Question;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.model.entity.User;
import com.example.quiz.service.user.UserQuestionService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// todo fix this ish
public class UserQuestionControllerTest {

    @Mock
    private UserQuestionService userQuestionService;

    @Mock
    private UserQuizStateService userQuizStateService;

    @Mock
    private HttpSession session;

    @Mock
    private User user;

    @InjectMocks
    private UserQuestionController userQuestionController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetRandomQuestionWithShuffledAnswers_Success() {
        // Arrange
        Long userId = 1L;
        QuizState quizState = new QuizState(userId);
        Question question = new Question();
        GameEventDto questionGameEventDto = new QuestionGameEventDto();

        when(user.getId()).thenReturn(userId);
        when(userQuizStateService.getLatestQuizStateByUserId(userId)).thenReturn(Optional.of(quizState));
        when(userQuestionService.getRandomQuestionExcludingCompleted(quizState.getCompletedQuestionIds())).thenReturn(question);
        //when(userQuestionService.createQuestionGameEvent(question, quizState)).thenReturn(questionGameEventDto);

        // Act
        ResponseEntity<GameEventDto> response = userQuestionController.getRandomQuestionWithShuffledAnswers(session, user);

        // Assert
        assertEquals(ResponseEntity.ok(questionGameEventDto), response);
        verify(userQuizStateService, times(1)).getLatestQuizStateByUserId(userId);
        verify(userQuestionService, times(1)).getRandomQuestionExcludingCompleted(quizState.getCompletedQuestionIds());
        verify(userQuizStateService, times(1)).saveQuizState(quizState);
        verify(session, times(1)).setAttribute("quizState", quizState);
    }

    @Test
    public void testGetRandomQuestionWithShuffledAnswers_QuizStateNotFound() {
        // Arrange
        Long userId = 1L;

        when(user.getId()).thenReturn(userId);
        when(userQuizStateService.getLatestQuizStateByUserId(userId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<GameEventDto> response = userQuestionController.getRandomQuestionWithShuffledAnswers(session, user);

        // Assert
        assertEquals(ResponseEntity.badRequest().build(), response);
        verify(userQuizStateService, times(1)).getLatestQuizStateByUserId(userId);
        verify(userQuestionService, never()).getRandomQuestionExcludingCompleted(any());
        verify(userQuizStateService, never()).saveQuizState(any());
        verify(session, never()).setAttribute(anyString(), any());
    }
}
