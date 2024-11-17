package com.example.quiz.service.user;

import com.example.quiz.model.entity.Question;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.repository.QuizStateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserQuizStateServiceTest {

    @Mock
    private QuizStateRepository quizStateRepository;

    @Mock
    private UserQuestionService userQuestionService;

    @InjectMocks
    private UserQuizStateService userQuizStateService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testStartNewQuiz() {
        // Arrange
        Long userId = 1L;
        QuizState quizState = new QuizState(userId);
        when(quizStateRepository.save(any(QuizState.class))).thenReturn(quizState);

        // Act
        QuizState result = userQuizStateService.startNewQuiz(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        verify(quizStateRepository, times(1)).save(any(QuizState.class));
    }

    @Test
    public void testGetLatestQuizStateByUserId_Found() {
        // Arrange
        Long userId = 1L;
        QuizState quizState = new QuizState(userId);
        when(quizStateRepository.findFirstByUserIdOrderByIdDesc(userId)).thenReturn(Optional.of(quizState));

        // Act
        Optional<QuizState> result = userQuizStateService.getLatestQuizStateByUserId(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getUserId());
    }

    @Test
    public void testGetLatestQuizStateByUserId_NotFound() {
        // Arrange
        Long userId = 1L;
        when(quizStateRepository.findFirstByUserIdOrderByIdDesc(userId)).thenReturn(Optional.empty());

        // Act
        Optional<QuizState> result = userQuizStateService.getLatestQuizStateByUserId(userId);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testAddQuestion() {
        // Arrange
        QuizState quizState = new QuizState(1L);
        Question question = new Question();
        question.setId(1L);

        // Act
        userQuizStateService.addQuestion(quizState, question);

        // Assert
        assertTrue(quizState.getAllQuestions().contains(question));
        verify(quizStateRepository, times(1)).save(quizState);
    }

    @Test
    public void testIncrementScore() {
        // Arrange
        QuizState quizState = new QuizState(1L);
        double initialScore = quizState.getScore();

        // Act
        userQuizStateService.incrementScore(quizState);

        // Assert
        assertEquals(initialScore + 1, quizState.getScore());
        verify(quizStateRepository, times(1)).save(quizState);
    }

    @Test
    public void testMarkQuestionAsCompleted() {
        // Arrange
        QuizState quizState = new QuizState(1L);
        Long questionId = 1L;

        // Act
        userQuizStateService.markQuestionAsCompleted(quizState, questionId);

        // Assert
        assertTrue(quizState.getCompletedQuestionIds().contains(questionId));
        verify(quizStateRepository, times(1)).save(quizState);
    }
}
