<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD:SpringQuiz/src/test/java/com/example/quiz/service/admin/AdminQuestionServiceTest.java
//package com.example.quiz.service.admin;
//
//import com.example.quiz.exception.ResourceNotFoundException;
//import com.example.quiz.model.Answer;
//import com.example.quiz.model.CorrectAnswer;
//import com.example.quiz.model.MockAnswer;
//import com.example.quiz.model.Question;
//import com.example.quiz.model.QuestionDto;
//import com.example.quiz.model.QuestionWithShuffledAnswersDto;
//import com.example.quiz.repository.QuestionRepository;
//import com.example.quiz.service.admin.AdminQuestionService;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class AdminQuestionServiceTest {
//
//    @Mock
//    private QuestionRepository questionRepository;
//
//    @InjectMocks
//    private AdminQuestionService adminQuestionService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testCreateQuestionFromDto() {
//        // Arrange
//        QuestionDto questionDto = new QuestionDto();
//        questionDto.setQuestionText("What is the capital of France?");
//        questionDto.setRealAnswer("Paris");
//        questionDto.setMockAnswers(Arrays.asList("London", "Berlin", "Rome"));
//
//        Question question = new Question();
//        CorrectAnswer correctAnswer = new CorrectAnswer("Paris", question);
//        List<MockAnswer> mockAnswers = Arrays.asList(
//            new MockAnswer("London", question),
//            new MockAnswer("Berlin", question),
//            new MockAnswer("Rome", question)
//        );
//
//        question.setQuestionText("What is the capital of France?");
//        question.setCorrectAnswer(correctAnswer);
//        question.setMockAnswers(mockAnswers);
//
//        when(questionRepository.save(any(Question.class))).thenReturn(question);
//
//        // Act
//        QuestionDto result = adminQuestionService.createQuestionFromDto(questionDto);
//
//        // Assert
//        assertEquals("What is the capital of France?", result.getQuestionText());
//        assertEquals("Paris", result.getRealAnswer());
//        assertEquals(3, result.getMockAnswers().size());
//        verify(questionRepository, times(1)).save(any(Question.class));
//    }
//
//    @Test
//    void testGetQuestionById() {
//        // Arrange
//        Question question = new Question();
//        question.setQuestionText("What is the capital of France?");
//        question.setCorrectAnswer(new CorrectAnswer("Paris", question));
//        question.setMockAnswers(Arrays.asList(
//            new MockAnswer("London", question),
//            new MockAnswer("Berlin",  question),
//            new MockAnswer("Rome", question)
//        ));
//
//        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
//
//        // Act
//        QuestionDto result = adminQuestionService.getQuestionById(1L);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals("What is the capital of France?", result.getQuestionText());
//        assertEquals("Paris", result.getRealAnswer());
//    }
//
//    @Test
//    void testGetQuestionById_NotFound() {
//        // Arrange
//        when(questionRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(ResourceNotFoundException.class, () -> adminQuestionService.getQuestionById(1L));
//    }
//
//    @Test
//    void testConvertToDto() {
//        // Arrange
//        Question question = new Question();
//        question.setQuestionText("What is the capital of France?");
//        question.setCorrectAnswer(new CorrectAnswer("Paris", question));
//        question.setMockAnswers(Arrays.asList(
//            new MockAnswer("London", question),
//            new MockAnswer("Berlin",  question),
//            new MockAnswer("Rome", question)
//        ));
//
//        // Act
//        QuestionDto result = adminQuestionService.convertToDto(question);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals("What is the capital of France?", result.getQuestionText());
//        assertEquals("Paris", result.getRealAnswer());
//        assertEquals(3, result.getMockAnswers().size());
//    }
//
//
//}
=======
=======
>>>>>>> parent of d1b4186 (Updated Model)
=======
>>>>>>> parent of d1b4186 (Updated Model)
package com.example.quiz.service;

import com.example.quiz.exception.ResourceNotFoundException;
import com.example.quiz.model.Answer;
import com.example.quiz.model.Question;
import com.example.quiz.model.QuestionDto;
import com.example.quiz.model.QuestionWithShuffledAnswersDto;
import com.example.quiz.repository.QuestionRepository;
import com.example.quiz.service.admin.AdminQuestionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private AdminQuestionService adminQuestionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateQuestionFromDto() {
        // Arrange
        QuestionDto questionDto = new QuestionDto();
        questionDto.setQuestionText("What is the capital of France?");
        questionDto.setRealAnswer("Paris");
        questionDto.setMockAnswers(Arrays.asList("London", "Berlin", "Rome"));

        Question question = new Question();
        Answer realAnswer = new Answer("Paris", true, question);
        List<Answer> mockAnswers = Arrays.asList(
            new Answer("London", false, question),
            new Answer("Berlin", false, question),
            new Answer("Rome", false, question)
        );

        question.setQuestionText("What is the capital of France?");
        question.setRealAnswer(realAnswer);
        question.setMockAnswers(mockAnswers);

        when(questionRepository.save(any(Question.class))).thenReturn(question);

        // Act
        QuestionDto result = adminQuestionService.createQuestionFromDto(questionDto);

        // Assert
        assertEquals("What is the capital of France?", result.getQuestionText());
        assertEquals("Paris", result.getRealAnswer());
        assertEquals(3, result.getMockAnswers().size());
        verify(questionRepository, times(1)).save(any(Question.class));
    }

    @Test
    void testGetQuestionById() {
        // Arrange
        Question question = new Question();
        question.setQuestionText("What is the capital of France?");
        question.setRealAnswer(new Answer("Paris", true, question));
        question.setMockAnswers(Arrays.asList(
            new Answer("London", false, question),
            new Answer("Berlin", false, question),
            new Answer("Rome", false, question)
        ));

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        // Act
        QuestionDto result = adminQuestionService.getQuestionById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("What is the capital of France?", result.getQuestionText());
        assertEquals("Paris", result.getRealAnswer());
    }

    @Test
    void testGetQuestionById_NotFound() {
        // Arrange
        when(questionRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> adminQuestionService.getQuestionById(1L));
    }

    @Test
    void testConvertToDto() {
        // Arrange
        Question question = new Question();
        question.setQuestionText("What is the capital of France?");
        question.setRealAnswer(new Answer("Paris", true, question));
        question.setMockAnswers(Arrays.asList(
            new Answer("London", false, question),
            new Answer("Berlin", false, question),
            new Answer("Rome", false, question)
        ));

        // Act
        QuestionDto result = adminQuestionService.convertToDto(question);

        // Assert
        assertNotNull(result);
        assertEquals("What is the capital of France?", result.getQuestionText());
        assertEquals("Paris", result.getRealAnswer());
        assertEquals(3, result.getMockAnswers().size());
    }

    @Test
    void testGetRandomQuestionWithShuffledAnswers() {
        // Arrange
        Question question = new Question();
        question.setQuestionText("What is the capital of France?");
        question.setRealAnswer(new Answer("Paris", true, question));
        question.setMockAnswers(Arrays.asList(
            new Answer("London", false, question),
            new Answer("Berlin", false, question),
            new Answer("Rome", false, question)
        ));

        when(questionRepository.findRandomQuestion()).thenReturn(question);

        // Act
        QuestionWithShuffledAnswersDto result = adminQuestionService.getRandomQuestionWithShuffledAnswers();

        // Assert
        assertNotNull(result);
        assertEquals("What is the capital of France?", result.getQuestionText());
        assertEquals(4, result.getShuffledAnswers().size());  // 1 real answer + 3 mock answers
    }
}
<<<<<<< HEAD
<<<<<<< HEAD
>>>>>>> parent of d1b4186 (Updated Model):SpringQuiz/src/test/java/com/example/quiz/service/QuestionServiceTest.java
=======
>>>>>>> parent of d1b4186 (Updated Model)
=======
>>>>>>> parent of d1b4186 (Updated Model)
