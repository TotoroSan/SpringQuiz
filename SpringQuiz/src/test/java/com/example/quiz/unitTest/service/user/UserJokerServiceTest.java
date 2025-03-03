<<<<<<<< HEAD:SpringQuiz/src/test/java/com/example/quiz/service/user/UserJokerServiceTest.java
package com.example.quiz.service.user;
========
package com.example.quiz.unitTest.service.user;
>>>>>>>> 5234cadcc7e235fbc1e9c39b5f08340ea17707a7:SpringQuiz/src/test/java/com/example/quiz/unitTest/service/user/UserJokerServiceTest.java

import com.example.quiz.model.dto.JokerDto;
import com.example.quiz.model.entity.*;
import com.example.quiz.model.entity.Joker.*;
import com.example.quiz.repository.QuizStateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserJokerServiceTest {

    @Mock
    private QuizStateRepository quizStateRepository;

    @Mock
    private UserQuizStateService userQuizStateService;

    @InjectMocks
    private UserJokerService userJokerService;

    private QuizState quizState;
    private QuizModifier quizModifier;
    private HashMap<UUID, Joker> activeJokers;
    private Joker fiftyFiftyJoker;
    private Joker skipQuestionJoker;
    private Joker twentyFiveSeventyFiveJoker;
    private JokerMetaData jokerMetaData;
    private UUID jokerUuid;

    @BeforeEach
    void setUp() {
        // Initialize mock QuizState
        quizState = mock(QuizState.class);
        quizModifier = mock(QuizModifier.class);
        activeJokers = new HashMap<>();
        jokerUuid = UUID.randomUUID();

        // Stub methods
        when(quizState.getQuizModifier()).thenReturn(quizModifier);
        when(quizState.getOwnedJokers()).thenReturn(activeJokers);

        // Create sample jokers
        fiftyFiftyJoker = new FiftyFiftyJoker("FIFTY_FIFTY", "50/50", "Eliminates two wrong answers", 50, 1, 1, 1, quizState);
        skipQuestionJoker = new SkipQuestionJoker("SKIP_QUESTION", "Skip Question", "Skips current question", 50, 1, 1, 1, quizState);
        twentyFiveSeventyFiveJoker = new TwentyFiveSeventyFiveJoker("TWENTYFIVE_SEVENTYFIVE", "25/75", "Eliminates one wrong answer", 50, 1, 1, 1, quizState);

        // Create sample metadata for JokerFactory mock
        jokerMetaData = new JokerMetaData("FIFTY_FIFTY", "50/50", "Removes two wrong answers", 50, "gameplay", 1, 50, 1);
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
        // Arrange
        ShopGameEvent shopGameEvent = mock(ShopGameEvent.class);
        when(quizState.getLatestGameEvent()).thenReturn(shopGameEvent);
        when(shopGameEvent.getPresentedJokerIdStrings()).thenReturn(List.of("FIFTY_FIFTY"));
        when(quizModifier.getCash()).thenReturn(100);

        // Mock JokerFactory metadata registry
        Map<String, JokerMetaData> metaRegistry = new HashMap<>();
        metaRegistry.put("FIFTY_FIFTY", jokerMetaData);
        when(JokerFactory.getJokerMetadataRegistry()).thenReturn(metaRegistry);

        // Act
        boolean purchaseResult = userJokerService.purchaseJoker(quizState, "FIFTY_FIFTY", 1);

        // Assert
        assertTrue(purchaseResult);
        verify(quizModifier, times(1)).setCash(50);
        verify(quizStateRepository, times(1)).save(quizState);
    }

    @Test
    void testPurchaseJoker_Fails_NotEnoughCash() {
        // Arrange
        ShopGameEvent shopGameEvent = mock(ShopGameEvent.class);
        when(quizState.getLatestGameEvent()).thenReturn(shopGameEvent);
        when(shopGameEvent.getPresentedJokerIdStrings()).thenReturn(List.of("FIFTY_FIFTY"));
        when(quizModifier.getCash()).thenReturn(30); // Less than Joker cost

        // Mock JokerFactory metadata registry
        Map<String, JokerMetaData> metaRegistry = new HashMap<>();
        metaRegistry.put("FIFTY_FIFTY", jokerMetaData);
        when(JokerFactory.getJokerMetadataRegistry()).thenReturn(metaRegistry);

        // Act
        boolean purchaseResult = userJokerService.purchaseJoker(quizState, "FIFTY_FIFTY", 1);

        // Assert
        assertFalse(purchaseResult);
        verify(quizModifier, never()).setCash(anyInt());
        verify(quizStateRepository, never()).save(any());
    }

    @Test
    void testPurchaseJoker_Fails_NotShopEvent() {
        // Arrange
        QuestionGameEvent questionEvent = mock(QuestionGameEvent.class);
        when(quizState.getLatestGameEvent()).thenReturn(questionEvent);

        // Act
        boolean purchaseResult = userJokerService.purchaseJoker(quizState, "FIFTY_FIFTY", 1);

        // Assert
        assertFalse(purchaseResult);
        verify(quizModifier, never()).setCash(anyInt());
        verify(quizStateRepository, never()).save(any());
    }

    @Test
    void testPurchaseJoker_Fails_JokerNotInShop() {
        // Arrange
        ShopGameEvent shopGameEvent = mock(ShopGameEvent.class);
        when(quizState.getLatestGameEvent()).thenReturn(shopGameEvent);
        when(shopGameEvent.getPresentedJokerIdStrings()).thenReturn(List.of("SKIP_QUESTION")); // Different joker

        // Act
        boolean purchaseResult = userJokerService.purchaseJoker(quizState, "FIFTY_FIFTY", 1);

        // Assert
        assertFalse(purchaseResult);
        verify(quizModifier, never()).setCash(anyInt());
        verify(quizStateRepository, never()).save(any());
    }

    @Test
    void testPurchaseJoker_Fails_MetadataNotFound() {
        // Arrange
        ShopGameEvent shopGameEvent = mock(ShopGameEvent.class);
        when(quizState.getLatestGameEvent()).thenReturn(shopGameEvent);
        when(shopGameEvent.getPresentedJokerIdStrings()).thenReturn(List.of("FIFTY_FIFTY"));

        // Empty metadata registry
        Map<String, JokerMetaData> metaRegistry = new HashMap<>();
        when(JokerFactory.getJokerMetadataRegistry()).thenReturn(metaRegistry);

        // Act
        boolean purchaseResult = userJokerService.purchaseJoker(quizState, "FIFTY_FIFTY", 1);

        // Assert
        assertFalse(purchaseResult);
        verify(quizModifier, never()).setCash(anyInt());
        verify(quizStateRepository, never()).save(any());
    }

    @Test
    void testUseJoker_Successful() {
        // Arrange
        QuestionGameEvent questionGameEvent = mock(QuestionGameEvent.class);
        when(quizState.getGameEvents()).thenReturn(List.of(questionGameEvent));

        activeJokers.put(jokerUuid, fiftyFiftyJoker);
        fiftyFiftyJoker.setUses(1);

        // Mock behavior to make the effect apply successfully
        Question question = mock(Question.class);
        CorrectAnswer correctAnswer = mock(CorrectAnswer.class);
        when(correctAnswer.getId()).thenReturn(1L);
        when(question.getCorrectAnswer()).thenReturn(correctAnswer);
        when(quizState.getCurrentQuestion()).thenReturn(question);
        when(quizState.getLatestGameEvent()).thenReturn(questionGameEvent);

        List<Answer> shuffledAnswers = new ArrayList<>();
        MockAnswer mockAnswer1 = new MockAnswer("Answer A", question);
        mockAnswer1.setId(2L);
        MockAnswer mockAnswer2 = new MockAnswer("Answer B", question);
        mockAnswer2.setId(3L);
        MockAnswer mockAnswer3 = new MockAnswer("Answer C", question);
        mockAnswer3.setId(4L);
        shuffledAnswers.add(mockAnswer1);
        shuffledAnswers.add(mockAnswer2);
        shuffledAnswers.add(correctAnswer);
        shuffledAnswers.add(mockAnswer3);

        when(questionGameEvent.getShuffledAnswers()).thenReturn(shuffledAnswers);
        when(questionGameEvent.getEliminatedAnswerIds()).thenReturn(new ArrayList<>());

        // Act
        boolean result = userJokerService.useJoker(quizState, jokerUuid);

        // Assert
        assertTrue(result);
        assertFalse(activeJokers.containsKey(jokerUuid));
        verify(quizStateRepository, times(1)).save(quizState);
    }

    @Test
    void testUseJoker_Fails_JokerNotFound() {
        // Arrange
        UUID unknownJokerId = UUID.randomUUID();

        // Act
        boolean result = userJokerService.useJoker(quizState, unknownJokerId);

        // Assert
        assertFalse(result);
        verify(quizStateRepository, never()).save(any());
    }

    @Test
    void testUseJoker_Fails_NoUsesLeft() {
        // Arrange
        activeJokers.put(jokerUuid, fiftyFiftyJoker);
        fiftyFiftyJoker.setUses(0);

        // Act
        boolean result = userJokerService.useJoker(quizState, jokerUuid);

        // Assert
        assertFalse(result);
        verify(quizStateRepository, never()).save(any());
    }

    @Test
    void testUseJoker_Fails_NotQuestionEvent() {
        // Arrange
        activeJokers.put(jokerUuid, fiftyFiftyJoker);
        fiftyFiftyJoker.setUses(1);

        // Mock a non-question event
        ShopGameEvent shopEvent = mock(ShopGameEvent.class);
        when(quizState.getGameEvents()).thenReturn(List.of(shopEvent));

        // Act
        boolean result = userJokerService.useJoker(quizState, jokerUuid);

        // Assert
        assertFalse(result);
        verify(quizStateRepository, never()).save(any());
    }

    @Test
    void testUseJoker_MultipleUses() {
        // Arrange
        QuestionGameEvent questionGameEvent = mock(QuestionGameEvent.class);
        when(quizState.getGameEvents()).thenReturn(List.of(questionGameEvent));

        activeJokers.put(jokerUuid, fiftyFiftyJoker);
        fiftyFiftyJoker.setUses(2); // Multiple uses

        // Mock behavior to make the effect apply successfully
        Question question = mock(Question.class);
        CorrectAnswer correctAnswer = mock(CorrectAnswer.class);
        when(correctAnswer.getId()).thenReturn(1L);
        when(question.getCorrectAnswer()).thenReturn(correctAnswer);
        when(quizState.getCurrentQuestion()).thenReturn(question);
        when(quizState.getLatestGameEvent()).thenReturn(questionGameEvent);

        List<Answer> shuffledAnswers = new ArrayList<>();
        shuffledAnswers.add(new MockAnswer("Answer A", question));
        shuffledAnswers.add(new MockAnswer("Answer B", question));
        shuffledAnswers.add(correctAnswer);
        shuffledAnswers.add(new MockAnswer("Answer C", question));

        when(questionGameEvent.getShuffledAnswers()).thenReturn(shuffledAnswers);
        when(questionGameEvent.getEliminatedAnswerIds()).thenReturn(new ArrayList<>());

        // Act
        boolean result = userJokerService.useJoker(quizState, jokerUuid);

        // Assert
        assertTrue(result);
        assertTrue(activeJokers.containsKey(jokerUuid)); // Joker should still be there
        assertEquals(1, fiftyFiftyJoker.getUses());      // But with 1 fewer use
        verify(quizStateRepository, times(1)).save(quizState);
    }

    @Test
    void testApplyFiftyFiftyJoker_Successful() {
        // Arrange
        QuestionGameEvent questionGameEvent = mock(QuestionGameEvent.class);
        Question question = mock(Question.class);
        CorrectAnswer correctAnswer = mock(CorrectAnswer.class);
        when(correctAnswer.getId()).thenReturn(1L);
        when(question.getCorrectAnswer()).thenReturn(correctAnswer);

        List<Answer> shuffledAnswers = new ArrayList<>();
        MockAnswer mockAnswer1 = new MockAnswer("Answer A", question);
        mockAnswer1.setId(2L);
        MockAnswer mockAnswer2 = new MockAnswer("Answer B", question);
        mockAnswer2.setId(3L);
        MockAnswer mockAnswer3 = new MockAnswer("Answer C", question);
        mockAnswer3.setId(4L);
        shuffledAnswers.add(mockAnswer1);
        shuffledAnswers.add(mockAnswer2);
        shuffledAnswers.add(correctAnswer);
        shuffledAnswers.add(mockAnswer3);

        when(quizState.getLatestGameEvent()).thenReturn(questionGameEvent);
        when(quizState.getCurrentQuestion()).thenReturn(question);
        when(questionGameEvent.getShuffledAnswers()).thenReturn(shuffledAnswers);
        when(questionGameEvent.getEliminatedAnswerIds()).thenReturn(new ArrayList<>());

        // Act
        boolean result = userJokerService.applyFiftyFiftyJoker(quizState, fiftyFiftyJoker);

        // Assert
        assertTrue(result);
        verify(quizStateRepository, times(1)).save(quizState);
        assertEquals(2, questionGameEvent.getEliminatedAnswerIds().size());
    }

    @Test
    void testApplyFiftyFiftyJoker_Fails_NoLatestEvent() {
        // Arrange
        when(quizState.getLatestGameEvent()).thenReturn(null);

        // Act
        boolean result = userJokerService.applyFiftyFiftyJoker(quizState, fiftyFiftyJoker);

        // Assert
        assertFalse(result);
        verify(quizStateRepository, never()).save(any());
    }

    @Test
    void testApplyFiftyFiftyJoker_Fails_NotQuestionEvent() {
        // Arrange
        ShopGameEvent shopEvent = mock(ShopGameEvent.class);
        when(quizState.getLatestGameEvent()).thenReturn(shopEvent);

        // Act
        boolean result = userJokerService.applyFiftyFiftyJoker(quizState, fiftyFiftyJoker);

        // Assert
        assertFalse(result);
        verify(quizStateRepository, never()).save(any());
    }

    @Test
    void testApplyFiftyFiftyJoker_Fails_NoQuestion() {
        // Arrange
        QuestionGameEvent questionGameEvent = mock(QuestionGameEvent.class);
        when(quizState.getLatestGameEvent()).thenReturn(questionGameEvent);
        when(quizState.getCurrentQuestion()).thenReturn(null); // No question

        // Act
        boolean result = userJokerService.applyFiftyFiftyJoker(quizState, fiftyFiftyJoker);

        // Assert
        assertFalse(result);
        verify(quizStateRepository, never()).save(any());
    }

    @Test
    void testApplyFiftyFiftyJoker_Fails_NotEnoughWrongAnswers() {
        // Arrange
        QuestionGameEvent questionGameEvent = mock(QuestionGameEvent.class);
        Question question = mock(Question.class);
        CorrectAnswer correctAnswer = mock(CorrectAnswer.class);
        when(correctAnswer.getId()).thenReturn(1L);
        when(question.getCorrectAnswer()).thenReturn(correctAnswer);

        List<Answer> shuffledAnswers = new ArrayList<>();
        MockAnswer mockAnswer1 = new MockAnswer("Answer A", question);
        mockAnswer1.setId(2L);
        shuffledAnswers.add(mockAnswer1);
        shuffledAnswers.add(correctAnswer); // Only 1 wrong answer, need 2 for 50/50

        when(quizState.getLatestGameEvent()).thenReturn(questionGameEvent);
        when(quizState.getCurrentQuestion()).thenReturn(question);
        when(questionGameEvent.getShuffledAnswers()).thenReturn(shuffledAnswers);

        // Act
        boolean result = userJokerService.applyFiftyFiftyJoker(quizState, fiftyFiftyJoker);

        // Assert
        assertFalse(result);
        verify(quizStateRepository, never()).save(any());
    }

    @Test
    void testApplyTwentyFiveSeventyFiveJoker_Successful() {
        // Arrange
        QuestionGameEvent questionGameEvent = mock(QuestionGameEvent.class);
        Question question = mock(Question.class);
        CorrectAnswer correctAnswer = mock(CorrectAnswer.class);
        when(correctAnswer.getId()).thenReturn(1L);
        when(question.getCorrectAnswer()).thenReturn(correctAnswer);

        List<Answer> shuffledAnswers = new ArrayList<>();
        MockAnswer mockAnswer1 = new MockAnswer("Answer A", question);
        mockAnswer1.setId(2L);
        MockAnswer mockAnswer2 = new MockAnswer("Answer B", question);
        mockAnswer2.setId(3L);
        shuffledAnswers.add(mockAnswer1);
        shuffledAnswers.add(mockAnswer2);
        shuffledAnswers.add(correctAnswer);

        when(quizState.getLatestGameEvent()).thenReturn(questionGameEvent);
        when(quizState.getCurrentQuestion()).thenReturn(question);
        when(questionGameEvent.getShuffledAnswers()).thenReturn(shuffledAnswers);
        when(questionGameEvent.getEliminatedAnswerIds()).thenReturn(new ArrayList<>());

        // Act
        boolean result = userJokerService.applyTwentyFiveSeventyFiveJoker(quizState, twentyFiveSeventyFiveJoker);

        // Assert
        assertTrue(result);
        verify(quizStateRepository, times(1)).save(quizState);
        assertEquals(1, questionGameEvent.getEliminatedAnswerIds().size());
    }

    @Test
    void testApplyTwentyFiveSeventyFiveJoker_Fails_NoLatestEvent() {
        // Arrange
        when(quizState.getLatestGameEvent()).thenReturn(null);

        // Act
        boolean result = userJokerService.applyTwentyFiveSeventyFiveJoker(quizState, twentyFiveSeventyFiveJoker);

        // Assert
        assertFalse(result);
        verify(quizStateRepository, never()).save(any());
    }

    @Test
    void testApplyTwentyFiveSeventyFiveJoker_Fails_NotQuestionEvent() {
        // Arrange
        ShopGameEvent shopEvent = mock(ShopGameEvent.class);
        when(quizState.getLatestGameEvent()).thenReturn(shopEvent);

        // Act
        boolean result = userJokerService.applyTwentyFiveSeventyFiveJoker(quizState, twentyFiveSeventyFiveJoker);

        // Assert
        assertFalse(result);
        verify(quizStateRepository, never()).save(any());
    }

    @Test
    void testApplyTwentyFiveSeventyFiveJoker_Fails_NoQuestion() {
        // Arrange
        QuestionGameEvent questionGameEvent = mock(QuestionGameEvent.class);
        when(quizState.getLatestGameEvent()).thenReturn(questionGameEvent);
        when(quizState.getCurrentQuestion()).thenReturn(null); // No question

        // Act
        boolean result = userJokerService.applyTwentyFiveSeventyFiveJoker(quizState, twentyFiveSeventyFiveJoker);

        // Assert
        assertFalse(result);
        verify(quizStateRepository, never()).save(any());
    }

    @Test
    void testApplyTwentyFiveSeventyFiveJoker_Fails_NoWrongAnswers() {
        // Arrange
        QuestionGameEvent questionGameEvent = mock(QuestionGameEvent.class);
        Question question = mock(Question.class);
        CorrectAnswer correctAnswer = mock(CorrectAnswer.class);
        when(correctAnswer.getId()).thenReturn(1L);
        when(question.getCorrectAnswer()).thenReturn(correctAnswer);

        List<Answer> shuffledAnswers = new ArrayList<>();
        shuffledAnswers.add(correctAnswer); // Only the correct answer, no wrong answers

        when(quizState.getLatestGameEvent()).thenReturn(questionGameEvent);
        when(quizState.getCurrentQuestion()).thenReturn(question);
        when(questionGameEvent.getShuffledAnswers()).thenReturn(shuffledAnswers);

        // Act
        boolean result = userJokerService.applyTwentyFiveSeventyFiveJoker(quizState, twentyFiveSeventyFiveJoker);

        // Assert
        assertFalse(result);
        verify(quizStateRepository, never()).save(any());
    }

    @Test
    void testApplySkipQuestionJoker_Successful() {
        // Arrange
        QuestionGameEvent questionGameEvent = mock(QuestionGameEvent.class);
        when(quizState.getLatestGameEvent()).thenReturn(questionGameEvent);

        // Act
        boolean result = userJokerService.applySkipQuestionJoker(quizState, skipQuestionJoker);

        // Assert
        assertTrue(result);
        verify(questionGameEvent, times(1)).setSkipUsed(true);
        verify(quizStateRepository, times(1)).save(quizState);
    }

    @Test
    void testApplySkipQuestionJoker_Fails_NoLatestEvent() {
        // Arrange
        when(quizState.getLatestGameEvent()).thenReturn(null);

        // Act
        boolean result = userJokerService.applySkipQuestionJoker(quizState, skipQuestionJoker);

        // Assert
        assertFalse(result);
        verify(quizStateRepository, never()).save(any());
    }

    @Test
    void testApplySkipQuestionJoker_Fails_NotQuestionEvent() {
        // Arrange
        ShopGameEvent shopEvent = mock(ShopGameEvent.class);
        when(quizState.getLatestGameEvent()).thenReturn(shopEvent);

        // Act
        boolean result = userJokerService.applySkipQuestionJoker(quizState, skipQuestionJoker);

        // Assert
        assertFalse(result);
        verify(quizStateRepository, never()).save(any());
    }

    @Test
    void testGetOwnedJokerDtos() {
        // Arrange
        activeJokers.put(jokerUuid, fiftyFiftyJoker);

        UUID jokerUuid2 = UUID.randomUUID();
        activeJokers.put(jokerUuid2, skipQuestionJoker);

        // Act
        List<JokerDto> jokerDtos = userJokerService.getOwnedJokerDtos(quizState);

        // Assert
        assertEquals(2, jokerDtos.size());

        // Verify both jokers are in the returned DTOs
        assertTrue(jokerDtos.stream()
                .anyMatch(dto -> dto.getIdString().equals("FIFTY_FIFTY")));
        assertTrue(jokerDtos.stream()
                .anyMatch(dto -> dto.getIdString().equals("SKIP_QUESTION")));
    }

    @Test
    void testGetOwnedJokerDtos_EmptyList() {
        // Arrange - empty jokers map

        // Act
        List<JokerDto> jokerDtos = userJokerService.getOwnedJokerDtos(quizState);

        // Assert
        assertTrue(jokerDtos.isEmpty());
    }

    @Test
    void testConvertToDto() {
        // Arrange - prepare a joker
        UUID testUuid = UUID.randomUUID();
        Joker testJoker = new FiftyFiftyJoker("TEST_ID", "Test Name", "Test Description",
                75, 3, 2, 4, quizState);

        // Create a field accessor to set the UUID since it's normally set in constructor
        // This is a bit of a hack for testing purposes
        try {
            java.lang.reflect.Field field = Joker.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(testJoker, testUuid);
        } catch (Exception e) {
            fail("Failed to set joker UUID: " + e.getMessage());
        }

        // Act
        JokerDto resultDto = userJokerService.convertToDto(testJoker);

        // Assert
        assertEquals(testUuid, resultDto.getUuid());
        assertEquals("TEST_ID", resultDto.getIdString());
        assertEquals("Test Name", resultDto.getName());
        assertEquals("Test Description", resultDto.getDescription());
        assertEquals(75, resultDto.getCost());
        assertEquals(3, resultDto.getUses());
        assertEquals(2, resultDto.getRarity());
        assertEquals(4, resultDto.getTier());
    }
}