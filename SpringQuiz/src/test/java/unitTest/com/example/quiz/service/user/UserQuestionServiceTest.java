package unitTest.com.example.quiz.service.user;

import com.example.quiz.model.dto.QuestionDto;
import com.example.quiz.model.entity.*;
import com.example.quiz.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserQuestionServiceTest {

    @InjectMocks
    private UserQuestionService userQuestionService;

    @Mock
    private QuestionRepository questionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConvertToDto() {
        // Arrange
        CorrectAnswer correctAnswer = new CorrectAnswer();
        correctAnswer.setAnswerText("Correct Answer");

        MockAnswer mockAnswer1 = new MockAnswer();
        mockAnswer1.setAnswerText("Mock Answer 1");

        MockAnswer mockAnswer2 = new MockAnswer();
        mockAnswer2.setAnswerText("Mock Answer 2");

        Question question = new Question();
        question.setQuestionText("Sample Question");
        question.setCorrectAnswer(correctAnswer);
        question.setMockAnswers(List.of(mockAnswer1, mockAnswer2));

        // Act
        QuestionDto result = userQuestionService.convertToDto(question);

        // Assert
        assertNotNull(result);
        assertEquals("Sample Question", result.getQuestionText());
        assertEquals("Correct Answer", result.getRealAnswer());
        assertTrue(result.getMockAnswers().containsAll(Arrays.asList("Mock Answer 1", "Mock Answer 2")));
    }

    @Test
    void testGetRandomQuestion_NoTopic() {
        // Arrange
        Question mockQuestion = new Question();
        when(questionRepository.findRandomQuestion()).thenReturn(mockQuestion);

        // Act
        Question result = userQuestionService.getRandomQuestion();

        // Assert
        assertNotNull(result);
        assertEquals(mockQuestion, result);
        verify(questionRepository, times(1)).findRandomQuestion();
    }

    @Test
    void testGetRandomQuestion_WithTopic() {
        // Arrange
        String topic = "Science";
        Question mockQuestion = new Question();
        when(questionRepository.findRandomQuestion(topic)).thenReturn(mockQuestion);

        // Act
        Question result = userQuestionService.getRandomQuestion(topic);

        // Assert
        assertNotNull(result);
        assertEquals(mockQuestion, result);
        verify(questionRepository, times(1)).findRandomQuestion(topic);
    }

    @Test
    void testGetRandomQuestionExcludingCompleted() {
        // Arrange
        Set<Long> completedQuestionIds = Set.of(1L, 2L);
        Question mockQuestion = new Question();
        Page<Question> questionPage = new PageImpl<>(List.of(mockQuestion));
        Pageable pageable = Pageable.ofSize(1);
        when(questionRepository.findRandomQuestionExcludingCompleted(completedQuestionIds, pageable)).thenReturn(questionPage);

        // Act
        Question result = userQuestionService.getRandomQuestionExcludingCompleted(completedQuestionIds);

        // Assert
        assertNotNull(result);
        assertEquals(mockQuestion, result);
        verify(questionRepository, times(1)).findRandomQuestionExcludingCompleted(completedQuestionIds, pageable);
    }

    @Test
    void testGetRandomQuestionExcludingCompleted_EmptyPage() {
        // Arrange
        Set<Long> completedQuestionIds = Set.of(1L, 2L);
        Page<Question> emptyPage = new PageImpl<>(Collections.emptyList());
        Pageable pageable = Pageable.ofSize(1);
        when(questionRepository.findRandomQuestionExcludingCompleted(completedQuestionIds, pageable)).thenReturn(emptyPage);

        // Act
        Question result = userQuestionService.getRandomQuestionExcludingCompleted(completedQuestionIds);

        // Assert
        assertNull(result);
        verify(questionRepository, times(1)).findRandomQuestionExcludingCompleted(completedQuestionIds, pageable);
    }

    @Test
    void testCreateQuestionGameEvent() {
        // Arrange
        CorrectAnswer correctAnswer = new CorrectAnswer();
        correctAnswer.setId(1L);
        correctAnswer.setAnswerText("Correct Answer");

        MockAnswer mockAnswer1 = new MockAnswer();
        mockAnswer1.setId(2L);
        mockAnswer1.setAnswerText("Mock Answer 1");

        MockAnswer mockAnswer2 = new MockAnswer();
        mockAnswer2.setId(3L);
        mockAnswer2.setAnswerText("Mock Answer 2");

        Question question = new Question();
        question.setId(100L);
        question.setQuestionText("Sample Question");
        question.setCorrectAnswer(correctAnswer);
        question.setMockAnswers(List.of(mockAnswer1, mockAnswer2));

        QuizState quizState = new QuizState();

        // Act
        QuestionGameEvent result = userQuestionService.createQuestionGameEvent(question, quizState);

        // Assert
        assertNotNull(result);
        assertEquals(quizState, result.getQuizState());
        assertEquals(100L, result.getQuestionId());
        assertEquals("Sample Question", result.getQuestionText());
        assertEquals(3, result.getShuffledAnswers().size()); // Correct + 2 mock answers
    }
}
