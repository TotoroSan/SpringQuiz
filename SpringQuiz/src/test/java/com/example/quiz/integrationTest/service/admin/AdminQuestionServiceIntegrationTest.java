package com.example.quiz.integrationTest.service.admin;

import com.example.quiz.model.dto.QuestionDto;
import com.example.quiz.model.entity.Question;
import com.example.quiz.repository.QuestionRepository;
import com.example.quiz.service.admin.AdminQuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AdminQuestionServiceIntegrationTest {

    @Autowired
    private AdminQuestionService adminQuestionService;

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void testCreateQuestionFromDto() {
        // Arrange
        QuestionDto questionDto = new QuestionDto();
        questionDto.setQuestionText("What is the capital of France?");
        questionDto.setRealAnswer("Paris");
        questionDto.setMockAnswers(Arrays.asList("London", "Berlin", "Madrid"));

        // Act
        QuestionDto createdDto = adminQuestionService.createQuestionFromDto(questionDto);

        // Assert
        assertNotNull(createdDto);
        assertEquals("What is the capital of France?", createdDto.getQuestionText());
        assertEquals("Paris", createdDto.getRealAnswer());
        assertEquals(3, createdDto.getMockAnswers().size());
        assertTrue(createdDto.getMockAnswers().contains("London"));
        assertTrue(createdDto.getMockAnswers().contains("Berlin"));
        assertTrue(createdDto.getMockAnswers().contains("Madrid"));
    }

    @Test
    public void testUpdateQuestionFromDto() {
        // Arrange - Create a question first
        QuestionDto initialDto = new QuestionDto();
        initialDto.setQuestionText("Initial question?");
        initialDto.setRealAnswer("Initial answer");
        initialDto.setMockAnswers(Arrays.asList("Wrong 1", "Wrong 2"));

        QuestionDto createdDto = adminQuestionService.createQuestionFromDto(initialDto);

        // Prepare update data
        QuestionDto updateDto = new QuestionDto();
        updateDto.setQuestionText("Updated question?");
        updateDto.setRealAnswer("Updated answer");
        updateDto.setMockAnswers(Arrays.asList("New wrong 1", "New wrong 2"));

        // Act
        Long questionId = questionRepository.findAll().stream()
                .filter(q -> q.getQuestionText().equals("Initial question?"))
                .findFirst()
                .map(Question::getId)
                .orElse(null);

        assertNotNull(questionId, "Question ID should not be null");

        QuestionDto updatedDto = adminQuestionService.updateQuestionFromDto(questionId, updateDto);

        // Assert
        assertEquals("Updated question?", updatedDto.getQuestionText());
        assertEquals("Updated answer", updatedDto.getRealAnswer());
        assertEquals(2, updatedDto.getMockAnswers().size());
        assertTrue(updatedDto.getMockAnswers().contains("New wrong 1"));
        assertTrue(updatedDto.getMockAnswers().contains("New wrong 2"));
    }

    @Test
    public void testGetQuestionById() {
        // Arrange - Create a question first
        QuestionDto questionDto = new QuestionDto();
        questionDto.setQuestionText("Test question?");
        questionDto.setRealAnswer("Test answer");
        questionDto.setMockAnswers(Arrays.asList("Wrong 1", "Wrong 2"));

        adminQuestionService.createQuestionFromDto(questionDto);

        Long questionId = questionRepository.findAll().stream()
                .filter(q -> q.getQuestionText().equals("Test question?"))
                .findFirst()
                .map(Question::getId)
                .orElse(null);

        assertNotNull(questionId, "Question ID should not be null");

        // Act
        QuestionDto retrievedDto = adminQuestionService.getQuestionById(questionId);

        // Assert
        assertEquals("Test question?", retrievedDto.getQuestionText());
        assertEquals("Test answer", retrievedDto.getRealAnswer());
        assertEquals(2, retrievedDto.getMockAnswers().size());
    }

    @Test
    public void testDeleteQuestionById() {
        // Arrange - Create a question first
        QuestionDto questionDto = new QuestionDto();
        questionDto.setQuestionText("Question to delete?");
        questionDto.setRealAnswer("Delete answer");
        questionDto.setMockAnswers(Arrays.asList("Delete 1", "Delete 2"));

        adminQuestionService.createQuestionFromDto(questionDto);

        Long questionId = questionRepository.findAll().stream()
                .filter(q -> q.getQuestionText().equals("Question to delete?"))
                .findFirst()
                .map(Question::getId)
                .orElse(null);

        assertNotNull(questionId, "Question ID should not be null");

        // Act
        adminQuestionService.deleteQuestionById(questionId);

        // Assert
        Optional<Question> deletedQuestion = questionRepository.findById(questionId);
        assertTrue(deletedQuestion.isEmpty(), "Question should be deleted");
    }
}