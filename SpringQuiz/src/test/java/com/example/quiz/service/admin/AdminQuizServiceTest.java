package com.example.quiz.service.admin;

import com.example.quiz.model.Quiz;
import com.example.quiz.repository.QuizStateRepository;
import com.example.quiz.service.admin.AdminQuizService;

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

public class AdminQuizServiceTest {

    @Mock
    private QuizStateRepository quizStateRepository; // object which should be mocked (i.e. instead of actually calling the function a mock object will be returned)

    @InjectMocks
    private AdminQuizService adminQuizService; // use the mock objects for testing the service 

    @BeforeEach
    public void setup() {
        // Initializes the @Mock and @InjectMocks objects
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateQuiz() {
        // Arrange
        Quiz quiz = new Quiz();
        quiz.setTitle("Sample Quiz");

        // Mock the save method => this basically means: if someone calls save on the mock repository, then return quiz (our "mock" data) this way save is not really called but instead a mock object is returned 
        // this way we can really test create quiz in an isolated manner 
        when(quizStateRepository.save(any(Quiz.class))).thenReturn(quiz);

        // Act
        Quiz createdQuiz = adminQuizService.createQuiz(quiz);

        // Assert
        assertEquals("Sample Quiz", createdQuiz.getTitle());
        verify(quizStateRepository, times(1)).save(quiz);
    }

    @Test
    public void testGetAllQuizzes() {
        // Arrange
        List<Quiz> quizzes = Arrays.asList(new Quiz("Sample Quiz 1"), new Quiz("Sample Quiz 2"));
        when(quizStateRepository.findAll()).thenReturn(quizzes);

        // Act
        List<Quiz> result = adminQuizService.getAllQuizzes();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Sample Quiz 1", result.get(0).getTitle());
        verify(quizStateRepository, times(1)).findAll();
    }

//    @Test // todo this function doesnt exist yet
//    public void testGetQuizById_NotFound() {
//        // Arrange
//        when(quizRepository.findById(1L)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(RuntimeException.class, () -> {
//            quizService.getQuizById(1L);
//        });
//    }
}
