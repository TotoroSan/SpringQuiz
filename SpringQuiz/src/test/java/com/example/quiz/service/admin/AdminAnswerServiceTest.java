package com.example.quiz.service.admin;

import com.example.quiz.exception.ResourceNotFoundException;
import com.example.quiz.model.entity.Answer;
import com.example.quiz.model.entity.CorrectAnswer;
import com.example.quiz.repository.AnswerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AdminAnswerServiceTest {

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private AdminAnswerService adminAnswerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateAnswer() {
        // Arrange
    	CorrectAnswer answer = new CorrectAnswer();
        answer.setAnswerText("Sample Answer");
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);

        // Act
        Answer createdAnswer = adminAnswerService.createAnswer(answer);

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
            adminAnswerService.getAnswerById(1L);
        });
    }
}
