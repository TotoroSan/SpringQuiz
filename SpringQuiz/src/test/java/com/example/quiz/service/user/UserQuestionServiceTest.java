package com.example.quiz.service.user;

import com.example.quiz.exception.ResourceNotFoundException;
import com.example.quiz.model.dto.AnswerDto;
import com.example.quiz.model.dto.QuestionWithShuffledAnswersDto;
import com.example.quiz.model.entity.CorrectAnswer;
import com.example.quiz.model.entity.MockAnswer;
import com.example.quiz.model.entity.Question;
import com.example.quiz.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class UserQuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private UserQuestionService userQuestionService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetRandomQuestion() {
        // Arrange
        Question question = new Question();
        question.setId(1L);
        question.setQuestionText("Sample Question");
        when(questionRepository.findRandomQuestion()).thenReturn(question);

        // Act
        Question result = userQuestionService.getRandomQuestion();

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Sample Question", result.getQuestionText());
    }

    @Test
    public void testGetRandomQuestionExcludingCompleted_Success() {
        // Arrange
        Set<Long> completedQuestionIds = new HashSet<>(Arrays.asList(1L, 2L));
        Question question = new Question();
        question.setId(3L);
        question.setQuestionText("New Question");
        Pageable pageable = PageRequest.of(0, 1);
        Page<Question> page = new PageImpl<>(Collections.singletonList(question), pageable, 1);

        when(questionRepository.findRandomQuestionExcludingCompleted(completedQuestionIds, pageable)).thenReturn(page);

        // Act
        Question result = userQuestionService.getRandomQuestionExcludingCompleted(completedQuestionIds);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("New Question", result.getQuestionText());
    }

    @Test
    public void testGetRandomQuestionExcludingCompleted_NotFound() {
        // Arrange
        Set<Long> completedQuestionIds = new HashSet<>(Arrays.asList(1L, 2L));
        Pageable pageable = PageRequest.of(0, 1);
        Page<Question> page = Page.empty();

        when(questionRepository.findRandomQuestionExcludingCompleted(completedQuestionIds, pageable)).thenReturn(page);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userQuestionService.getRandomQuestionExcludingCompleted(completedQuestionIds);
        });
    }

    @Test
    public void testCreateQuestionWithShuffledAnswersDto() {
        // Arrange
        Question question = new Question();
        question.setId(1L);
        question.setQuestionText("Sample Question");

        MockAnswer mockAnswer1 = new MockAnswer();
        mockAnswer1.setId(2L);
        mockAnswer1.setAnswerText("Mock Answer 1");

        MockAnswer mockAnswer2 = new MockAnswer();
        mockAnswer2.setId(3L);
        mockAnswer2.setAnswerText("Mock Answer 2");

        MockAnswer mockAnswer3 = new MockAnswer();
        mockAnswer3.setId(4L);
        mockAnswer3.setAnswerText("Mock Answer 3");

        question.setMockAnswers(Arrays.asList(mockAnswer1, mockAnswer2, mockAnswer3));

        AnswerDto correctAnswerDto = new AnswerDto(1L, "Correct Answer");
        question.setCorrectAnswer(new CorrectAnswer(correctAnswerDto.getText(), question));

        // Act
        QuestionWithShuffledAnswersDto result = userQuestionService.createQuestionWithShuffledAnswersDto(question);

        // Assert
        assertNotNull(result);
        assertEquals("Sample Question", result.getQuestionText());
        assertEquals(1L, result.getQuestionId());
        assertEquals(4, result.getShuffledAnswers().size());
    }
}
