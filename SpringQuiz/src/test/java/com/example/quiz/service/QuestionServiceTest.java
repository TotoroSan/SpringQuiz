package com.example.quiz.service;

import com.example.quiz.model.Question;
import com.example.quiz.repository.QuestionRepository;
import com.example.quiz.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuestionService questionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateQuestion() {
        // Arrange
        Question question = new Question();
        question.setQuestionText("Sample Question");
        when(questionRepository.save(any(Question.class))).thenReturn(question);

        // Act
        Question createdQuestion = questionService.createQuestion(question);

        // Assert
        assertEquals("Sample Question", createdQuestion.getQuestionText());
        verify(questionRepository, times(1)).save(question);
    }

    @Test
    public void testGetQuestionById_NotFound() {
        // Arrange
        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            questionService.getQuestionById(1L);
        });
    }

    @Test
    public void testDeleteQuestion() {
        // Arrange
        Question question = new Question();
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        // Act
        questionService.deleteQuestion(1L);

        // Assert
        verify(questionRepository, times(1)).delete(question);
    }
}
