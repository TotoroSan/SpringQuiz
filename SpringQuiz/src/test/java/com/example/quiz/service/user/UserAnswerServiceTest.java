package com.example.quiz.service.user;

import com.example.quiz.model.dto.AnswerDto;
import com.example.quiz.model.entity.CorrectAnswer;
import com.example.quiz.model.entity.Question;
import com.example.quiz.repository.AnswerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserAnswerServiceTest {

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private UserAnswerService userAnswerService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testIsCorrectAnswer_CorrectAnswer() {
        // Arrange
        Long answerId = 1L;
        AnswerDto answerDto = new AnswerDto(answerId, "Correct Answer");
        CorrectAnswer correctAnswer = new CorrectAnswer();
        correctAnswer.setId(answerId);

        Question question = new Question();
        question.setCorrectAnswer(correctAnswer);

        // Act
        Boolean isCorrect = userAnswerService.isCorrectAnswer(answerDto, question);

        // Assert
        assertTrue(isCorrect);
    }

    @Test
    public void testIsCorrectAnswer_WrongAnswer() {
        // Arrange
        Long answerId = 1L;
        Long wrongAnswerId = 2L;
        AnswerDto answerDto = new AnswerDto(wrongAnswerId, "Wrong Answer");
        CorrectAnswer correctAnswer = new CorrectAnswer();
        correctAnswer.setId(answerId);

        Question question = new Question();
        question.setCorrectAnswer(correctAnswer);

        // Act
        Boolean isCorrect = userAnswerService.isCorrectAnswer(answerDto, question);

        // Assert
        assertFalse(isCorrect);
    }
}
