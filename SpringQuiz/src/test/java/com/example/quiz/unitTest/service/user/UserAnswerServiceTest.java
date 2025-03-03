package com.example.quiz.unitTest.service.user;

import com.example.quiz.model.dto.AnswerDto;
import com.example.quiz.model.entity.CorrectAnswer;
import com.example.quiz.model.entity.MockAnswer;
import com.example.quiz.model.entity.Question;
import com.example.quiz.repository.AnswerRepository;
import com.example.quiz.service.user.UserAnswerService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class UserAnswerServiceTest {

    @InjectMocks
    private UserAnswerService userAnswerService;

    @Mock
    private AnswerRepository answerRepository;

    public UserAnswerServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIsCorrectAnswer_True() {
        // Arrange
        CorrectAnswer correctAnswer = new CorrectAnswer();
        correctAnswer.setId(1L);

        Question question = new Question();
        question.setCorrectAnswer(correctAnswer);

        AnswerDto answerDto = new AnswerDto(1L, "Correct Answer");

        // Act
        Boolean result = userAnswerService.isCorrectAnswer(answerDto, question);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsCorrectAnswer_False() {
        // Arrange
        CorrectAnswer correctAnswer = new CorrectAnswer();
        correctAnswer.setId(1L);

        Question question = new Question();
        question.setCorrectAnswer(correctAnswer);

        AnswerDto answerDto = new AnswerDto(2L, "Wrong Answer");

        // Act
        Boolean result = userAnswerService.isCorrectAnswer(answerDto, question);

        // Assert
        assertFalse(result);
    }

    @Test
    void testConvertToDto_WithCorrectAnswer() {
        // Arrange
        CorrectAnswer correctAnswer = new CorrectAnswer();
        correctAnswer.setId(1L);
        correctAnswer.setAnswerText("Correct Answer Text");

        // Act
        AnswerDto result = userAnswerService.convertToDto(correctAnswer);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Correct Answer Text", result.getText());
    }

    @Test
    void testConvertToDto_WithMockAnswer() {
        // Arrange
        MockAnswer mockAnswer = new MockAnswer();
        mockAnswer.setId(2L);
        mockAnswer.setAnswerText("Mock Answer Text");

        // Act
        AnswerDto result = userAnswerService.convertToDto(mockAnswer);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Mock Answer Text", result.getText());
    }
}
