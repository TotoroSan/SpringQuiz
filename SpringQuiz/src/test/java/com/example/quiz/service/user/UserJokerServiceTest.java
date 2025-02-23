package com.example.quiz.service.user;

import com.example.quiz.model.dto.JokerDto;
import com.example.quiz.model.entity.*;
import com.example.quiz.model.entity.Joker.*;
import com.example.quiz.repository.QuizStateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserJokerServiceTest {

    @Mock
    private QuizStateRepository quizStateRepository;

    @InjectMocks
    private UserJokerService userJokerService;

    private QuizState quizState;
    private QuizModifier quizModifier;
    private HashMap<UUID, Joker> activeJokers;
    private Joker fiftyFiftyJoker;
    private Joker skipQuestionJoker;
    private JokerMetaData jokerMetaData;

    @BeforeEach
    void setUp() {
        // Initialize mock QuizState
        quizState = mock(QuizState.class);
        quizModifier = mock(QuizModifier.class);
        activeJokers = new HashMap<>();

        // Stub methods
        when(quizState.getQuizModifier()).thenReturn(quizModifier);
        when(quizState.getOwnedJokers()).thenReturn(activeJokers);

        // Create sample jokers
        fiftyFiftyJoker = new FiftyFiftyJoker( "FIFTY_FIFTY", "test","test",50,1,1,1, quizState);
        skipQuestionJoker = new SkipQuestionJoker( "SKIP_QUESTION", "test","test",50,1,1,1, quizState);

        // Create sample metadata for JokerFactory mock
        jokerMetaData = new JokerMetaData("FIFTY_FIFTY", "50/50", "Removes two wrong answers", 50, "FIFTY_FIFTY", 1, 1, 1);
    }

    @Test
    void testPickRandomJokerDtos() {
        // Mock the JokerFactory behavior
        JokerFactory.getJokerMetadataRegistry().put("FIFTY_FIFTY", jokerMetaData);

        List<JokerDto> jokers = userJokerService.pickRandomJokerDtos();

        assertNotNull(jokers);
        assertFalse(jokers.isEmpty());
        assertEquals(1, jokers.size());
        assertEquals("FIFTY_FIFTY", jokers.get(0).getIdString());
    }

    @Test
    void testPurchaseJoker_Successful() {
        when(quizModifier.getCash()).thenReturn(100);

        boolean purchaseResult = userJokerService.purchaseJoker(quizState, "FIFTY_FIFTY", 1);

        assertTrue(purchaseResult);
        verify(quizModifier, times(1)).setCash(50);
        verify(quizStateRepository, times(1)).save(quizState);
    }

    @Test
    void testPurchaseJoker_Fails_NotEnoughCash() {
        when(quizModifier.getCash()).thenReturn(30); // Less than Joker cost

        boolean purchaseResult = userJokerService.purchaseJoker(quizState, "FIFTY_FIFTY", 1);

        assertFalse(purchaseResult);
        verify(quizModifier, never()).setCash(anyInt());
        verify(quizStateRepository, never()).save(any());
    }

    @Test
    void testUseJoker_Successful() {
        UUID jokerId = UUID.randomUUID();
        activeJokers.put(jokerId, fiftyFiftyJoker);
        fiftyFiftyJoker.setUses(1);

        boolean result = userJokerService.useJoker(quizState, jokerId);

        assertTrue(result);
        assertFalse(activeJokers.containsKey(jokerId));
        verify(quizStateRepository, times(1)).save(quizState);
    }

    @Test
    void testUseJoker_Fails_JokerNotFound() {
        UUID jokerId = UUID.randomUUID();
        boolean result = userJokerService.useJoker(quizState, jokerId);

        assertFalse(result);
        verify(quizStateRepository, never()).save(any());
    }

    @Test
    void testUseJoker_Fails_NoUsesLeft() {
        UUID jokerId = UUID.randomUUID();
        fiftyFiftyJoker.setUses(0);
        activeJokers.put(jokerId, fiftyFiftyJoker);

        boolean result = userJokerService.useJoker(quizState, jokerId);

        assertFalse(result);
        verify(quizStateRepository, never()).save(any());
    }

    @Test
    void testApplyFiftyFiftyJoker_Successful() {
        QuestionGameEvent questionGameEvent = mock(QuestionGameEvent.class);
        Question question = mock(Question.class);

        List<Answer> shuffledAnswers = new ArrayList<>();
        shuffledAnswers.add(new MockAnswer("Answer A", question));
        shuffledAnswers.add(new MockAnswer("Answer B", question));
        shuffledAnswers.add(new CorrectAnswer("Answer C", question)); // Correct answer
        shuffledAnswers.add(new MockAnswer("Answer D", question));

        when(quizState.getLatestGameEvent()).thenReturn(questionGameEvent);
        when(questionGameEvent.getShuffledAnswers()).thenReturn(shuffledAnswers);
        when(questionGameEvent.getEliminatedAnswerIds()).thenReturn(new ArrayList<>());

        userJokerService.applyFiftyFiftyJoker(quizState, fiftyFiftyJoker);

        verify(quizStateRepository, times(1)).save(quizState);
        assertEquals(2, questionGameEvent.getEliminatedAnswerIds().size());
    }

    @Test
    void testApplySkipQuestionJoker_Successful() {
        QuestionGameEvent questionGameEvent = mock(QuestionGameEvent.class);
        when(quizState.getLatestGameEvent()).thenReturn(questionGameEvent);

        userJokerService.applySkipQuestionJoker(quizState, skipQuestionJoker);

        verify(questionGameEvent, times(1)).setSkipUsed(true);
        verify(quizStateRepository, times(1)).save(quizState);
    }
}
