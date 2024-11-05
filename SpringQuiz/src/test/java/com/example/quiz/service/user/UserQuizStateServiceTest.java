package com.example.quiz.service.user;

import com.example.quiz.model.entity.QuizState;
import com.example.quiz.repository.QuizStateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserQuizStateServiceTest {

    @Mock
    private QuizStateRepository quizStateRepository; // Mock the repository for testing purposes

    @InjectMocks
    private UserQuizStateService userQuizStateService; // Service that is being tested, using the mock repository

    @BeforeEach
    public void setup() {
        // Initializes the @Mock and @InjectMocks objects
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testStartNewQuiz() {
        // Arrange
        Long userId = 1L;
        QuizState quizState = new QuizState(userId);

        // Mock the save method: when save is called on the repository, return the quizState
        when(quizStateRepository.save(any(QuizState.class))).thenReturn(quizState);

        // Act
        QuizState createdQuizState = userQuizStateService.startNewQuiz(userId);

        // Assert
        assertEquals(userId, createdQuizState.getUserId());
        verify(quizStateRepository, times(1)).save(quizState);
    }

    @Test
    public void testGetQuizStateByUserId_Found() {
        // Arrange
        Long userId = 1L;
        QuizState quizState = new QuizState(userId);
        when(quizStateRepository.findByUserId(userId)).thenReturn(Optional.of(quizState));

        // Act
        Optional<QuizState> result = userQuizStateService.getLatestQuizStateByUserId(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getUserId());
        verify(quizStateRepository, times(1)).findByUserId(userId);
    }

    @Test
    public void testGetQuizStateByUserId_NotFound() {
        // Arrange
        Long userId = 1L;
        when(quizStateRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act
        Optional<QuizState> result = userQuizStateService.getLatestQuizStateByUserId(userId);

        // Assert
        assertFalse(result.isPresent());
        verify(quizStateRepository, times(1)).findByUserId(userId);
    }
}
