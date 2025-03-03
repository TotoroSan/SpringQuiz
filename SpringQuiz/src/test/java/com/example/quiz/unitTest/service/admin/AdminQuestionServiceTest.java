package com.example.quiz.unitTest.service.admin;

import com.example.quiz.exception.ResourceNotFoundException;
import com.example.quiz.model.dto.QuestionDto;
import com.example.quiz.model.entity.CorrectAnswer;
import com.example.quiz.model.entity.MockAnswer;
import com.example.quiz.model.entity.Question;
import com.example.quiz.repository.QuestionRepository;
import com.example.quiz.service.admin.AdminQuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)

public class AdminQuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private AdminQuestionService adminQuestionService;

    private Question question;
    private QuestionDto questionDto;

    @BeforeEach
    void setUp() {
        question = new Question();
        question.setId(1L);
        question.setQuestionText("What is 2+2?");
        question.setCorrectAnswer(new CorrectAnswer("4", question));
        question.setMockAnswers(Arrays.asList(new MockAnswer("3", question), new MockAnswer("5", question)));

        questionDto = new QuestionDto();
        questionDto.setQuestionText("What is 2+2?");
        questionDto.setRealAnswer("4");
        questionDto.setMockAnswers(Arrays.asList("3", "5"));
    }

    @Test
    void testCreateQuestionFromDto() {
        when(questionRepository.save(any(Question.class))).thenReturn(question);
        when(questionRepository.getById(1L)).thenReturn(question);

        QuestionDto createdDto = adminQuestionService.createQuestionFromDto(questionDto);

        assertNotNull(createdDto);
        assertEquals(questionDto.getQuestionText(), createdDto.getQuestionText());
        assertEquals(questionDto.getRealAnswer(), createdDto.getRealAnswer());
        assertEquals(questionDto.getMockAnswers().size(), createdDto.getMockAnswers().size());

        verify(questionRepository, times(1)).save(any(Question.class));
    }

    @Test
    void testUpdateQuestionFromDto() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(questionRepository.save(any(Question.class))).thenReturn(question);

        QuestionDto updateDto = new QuestionDto();
        updateDto.setQuestionText("Updated question?");
        updateDto.setRealAnswer("Updated answer");
        updateDto.setMockAnswers(Arrays.asList("New wrong 1", "New wrong 2"));

        QuestionDto updatedDto = adminQuestionService.updateQuestionFromDto(1L, updateDto);

        assertNotNull(updatedDto);
        assertEquals(updateDto.getQuestionText(), updatedDto.getQuestionText());
        assertEquals(updateDto.getRealAnswer(), updatedDto.getRealAnswer());
        assertEquals(updateDto.getMockAnswers().size(), updatedDto.getMockAnswers().size());

        verify(questionRepository, times(1)).findById(1L);
        verify(questionRepository, times(1)).save(any(Question.class));
    }

    @Test
    void testGetQuestionById() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        QuestionDto retrievedDto = adminQuestionService.getQuestionById(1L);

        assertNotNull(retrievedDto);
        assertEquals(question.getQuestionText(), retrievedDto.getQuestionText());
        assertEquals(question.getCorrectAnswer().getAnswerText(), retrievedDto.getRealAnswer());
        assertEquals(question.getMockAnswers().size(), retrievedDto.getMockAnswers().size());

        verify(questionRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteQuestionById() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        adminQuestionService.deleteQuestionById(1L);

        verify(questionRepository, times(1)).findById(1L);
        verify(questionRepository, times(1)).delete(question);
    }

    @Test
    void testGetQuestionById_NotFound() {
        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminQuestionService.getQuestionById(1L));

        verify(questionRepository, times(1)).findById(1L);
    }
}