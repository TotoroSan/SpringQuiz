package com.example.quiz.service.user;

import com.example.quiz.model.dto.*;
import com.example.quiz.model.entity.*;
import com.example.quiz.repository.QuizStateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserQuizStateServiceTest {

    @InjectMocks
    private UserQuizStateService userQuizStateService;

    @Mock
    private QuizStateRepository quizStateRepository;

    @Mock
    private UserQuestionService userQuestionService;

    @Mock
    private UserQuizModifierService userQuizModifierService;

    @Mock
    private UserGameEventService userGameEventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testStartNewQuiz() {
        // Arrange
        Long userId = 1L;
        List<QuizState> activeQuizStates = List.of(new QuizState(userId));
        when(quizStateRepository.findAllByUserIdAndIsActiveTrue(userId)).thenReturn(activeQuizStates);

        // Act
        QuizState result = userQuizStateService.startNewQuiz(userId);

        // Assert
        assertNotNull(result);
        assertFalse(activeQuizStates.get(0).isActive());
        verify(quizStateRepository, times(1)).save(activeQuizStates.get(0));
        verify(quizStateRepository, times(1)).save(result);
    }

    @Test
    void testGetNextQuestion() {
        // Arrange
        QuizState quizState = new QuizState();
        quizState.setCurrentQuestionIndex(0);
        quizState.setAllQuestions(new ArrayList<>());

        Question mockQuestion = new Question();
        quizState.getAllQuestions().add(mockQuestion);

        // Act
        Question result = userQuizStateService.getNextQuestion(quizState);

        // Assert
        assertEquals(mockQuestion, result);
        assertEquals(1, quizState.getCurrentQuestionIndex());
        verify(quizStateRepository, times(1)).save(quizState);
    }

    @Test
    void testProcessCorrectAnswerSubmission() {
        // Arrange
        QuizState quizState = new QuizState();
        QuizModifier quizModifier = quizState.getQuizModifier(); // Mock values
        quizModifier.setBaseCashReward(10); // Mock base reward
        quizState.setQuizModifier(quizModifier);
        quizState.setCurrentQuestionIndex(0);

        Question mockQuestion = new Question();
        mockQuestion.setId(1L);
        mockQuestion.setDifficulty(2);
        quizState.setAllQuestions(List.of(mockQuestion));

        // Act
        userQuizStateService.processCorrectAnswerSubmission(quizState);

        // Assert
        verify(userQuizModifierService, times(1)).processActiveQuizModifierEffectsForNewRound(any());
        verify(quizStateRepository, times(1)).save(quizState);
        assertEquals(1, quizState.getCompletedQuestionIds().size());
        assertEquals(20, quizState.getQuizModifier().getCash()); // 10 * 2 = 20 cash earned
    }

    @Test
    void testValidateModifierChoiceEffectAgainstLastEvent_ValidChoice() {
        // Arrange
        QuizState quizState = new QuizState();
        UUID validUuid = UUID.randomUUID();
        ModifierEffectsGameEvent lastEvent = new ModifierEffectsGameEvent();
        lastEvent.setPresentedEffectUuids(List.of(validUuid));
        quizState.setGameEvents(new LinkedList<>(List.of(lastEvent)));

        // Act
        ModifierEffectsGameEvent result = userQuizStateService.validateModifierChoiceEffectAgainstLastEvent(quizState, validUuid);

        // Assert
        assertNotNull(result);
        assertEquals(lastEvent, result);
    }

    @Test
    void testCreateQuizSaveDto() {
        // Arrange
        QuizState quizState = new QuizState();
        quizState.setId(1L);

        QuestionGameEvent lastGameEvent = new QuestionGameEvent();
        lastGameEvent.setQuestionId(1L);
        quizState.setGameEvents(new LinkedList<>(List.of(lastGameEvent)));

        QuizStateDto mockQuizStateDto = new QuizStateDto(0, 0, "Question?", null, null, true);
        when(userGameEventService.convertToDto(lastGameEvent)).thenReturn(new QuestionGameEventDto());
        when(userQuizStateService.convertToDto(quizState)).thenReturn(mockQuizStateDto);

        // Act
        QuizSaveDto result = userQuizStateService.createQuizSaveDto(quizState);

        // Assert
        assertNotNull(result);
        assertEquals(mockQuizStateDto, result.getQuizStateDto());
        assertNotNull(result.getGameEventDto());
        assertTrue(result.getGameEventDto() instanceof QuestionGameEventDto);
    }

    @Test
    void testCreateQuizSaveDto_NoGameEvents() {
        // Arrange
        QuizState quizState = new QuizState();
        quizState.setId(1L);
        quizState.setGameEvents(new LinkedList<>()); // No events

        // Act
        QuizSaveDto result = userQuizStateService.createQuizSaveDto(quizState);

        // Assert
        assertNull(result);
    }
}
