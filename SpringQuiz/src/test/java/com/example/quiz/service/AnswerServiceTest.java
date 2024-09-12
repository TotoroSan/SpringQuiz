package com.example.quiz.service;

import com.example.quiz.model.Answer;
import com.example.quiz.repository.AnswerRepository;
import com.example.quiz.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AnswerServiceTest {

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private AnswerService answerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateAnswer() {
        // Arrange
        Answer answer = new Answer();
        answer.setAnswerText("Sample Answer");
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);

        // Act
        Answer createdAnswer = answerService.createAnswer(answer);

        // Assert
        assertEquals("Sample Answer", createdAnswer.getAnswerText());
        verify(answerRepository, times(1)).save(answer);
    }

    @Test
    public void testGetAnswerById_NotFound() {
        // Arrange
        when(answerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            answerService.getAnswerById(1L);
        });
    }
}
