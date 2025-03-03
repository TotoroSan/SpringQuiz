package com.example.quiz.unitTest.service.user;

import com.example.quiz.model.dto.*;
import com.example.quiz.model.entity.*;
import com.example.quiz.repository.GameEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserGameEventServiceTest {

    @InjectMocks
    private UserGameEventService userGameEventService;

    @Mock
    private UserAnswerService userAnswerService;

    @Mock
    private UserQuizModifierService userQuizModifierService;

    @Mock
    private GameEventRepository gameEventRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConvertToDto_QuestionGameEvent() {
        // Arrange
        QuestionGameEvent questionGameEvent = new QuestionGameEvent();
        questionGameEvent.setQuestionId(1L);
        questionGameEvent.setQuestionText("Sample Question");

        CorrectAnswer correctAnswer = new CorrectAnswer();
        correctAnswer.setId(101L);
        correctAnswer.setAnswerText("Correct Answer");

        questionGameEvent.setShuffledAnswers(List.of(correctAnswer));

        when(userAnswerService.convertToDto(correctAnswer)).thenReturn(new AnswerDto(101L, "Correct Answer"));

        // Act
        GameEventDto result = userGameEventService.convertToDto(questionGameEvent);

        // Assert
        assertTrue(result instanceof QuestionGameEventDto);
        QuestionGameEventDto dto = (QuestionGameEventDto) result;
        assertEquals(1L, dto.getQuestionId());
        assertEquals("Sample Question", dto.getQuestionText());
        assertEquals(1, dto.getShuffledAnswers().size());
        assertEquals("Correct Answer", dto.getShuffledAnswers().get(0).getText());
        verify(userAnswerService, times(1)).convertToDto(correctAnswer);
    }

    @Test
    void testConvertToDto_ModifierEffectsGameEvent() {
        // Arrange
        ModifierEffectsGameEvent modifierEffectsGameEvent = new ModifierEffectsGameEvent();
        UUID effectUuid = UUID.randomUUID();
        modifierEffectsGameEvent.setPresentedEffectUuids(List.of(effectUuid));
        modifierEffectsGameEvent.setPresentedEffectIdStrings(List.of("effect-1"));
        modifierEffectsGameEvent.setPresentedEffectTiers(List.of(1));
        modifierEffectsGameEvent.setPresentedEffectDurations(List.of(10));

        when(userQuizModifierService.convertToDto(
                eq(effectUuid),
                eq("effect-1"),
                eq("effect1"),
                eq(1),
                eq(10)
        )).thenReturn(new QuizModifierEffectDto(effectUuid, "effect-1", "effect1", 10,
                "effect1-desc", "effect", false, 1, 1));

        // Act
        GameEventDto result = userGameEventService.convertToDto(modifierEffectsGameEvent);

        // Assert
        assertTrue(result instanceof ModifierEffectsGameEventDto);
        ModifierEffectsGameEventDto dto = (ModifierEffectsGameEventDto) result;
        assertEquals(1, dto.getQuizModifierEffectDtos().size());
        assertEquals("effect-1", dto.getQuizModifierEffectDtos().get(0).getIdString());
        verify(userQuizModifierService, times(1)).convertToDto(eq(effectUuid), eq("effect-1"), eq("effect-1"), eq(1), eq(10));
    }

    @Test
    void testConvertToDto_UnknownGameEventType() {
        // Arrange
        GameEvent unknownEvent = mock(GameEvent.class);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userGameEventService.convertToDto(unknownEvent));
        assertTrue(exception.getMessage().contains("Unknown game event type"));
    }

    @Test
    void testResolveGameEvent_Success() {
        // Arrange
        GameEvent gameEvent = new GameEvent() {
            @Override
            public void setConsumed(boolean consumed) {
                super.setConsumed(consumed);
            }
        };
        gameEvent.setId(1L);
        gameEvent.setConsumed(false);

        when(gameEventRepository.findById(1L)).thenReturn(Optional.of(gameEvent));

        // Act
        userGameEventService.resolveGameEvent(1L);

        // Assert
        assertTrue(gameEvent.isConsumed());
        verify(gameEventRepository, times(1)).save(gameEvent);
    }

    @Test
    void testResolveGameEvent_AlreadyConsumed() {
        // Arrange
        GameEvent gameEvent = new GameEvent() {
            @Override
            public void setConsumed(boolean consumed) {
                super.setConsumed(consumed);
            }
        };
        gameEvent.setId(1L);
        gameEvent.setConsumed(true);

        when(gameEventRepository.findById(1L)).thenReturn(Optional.of(gameEvent));

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> userGameEventService.resolveGameEvent(1L));
        assertTrue(exception.getMessage().contains("GameEvent has already been resolved"));
    }

    @Test
    void testResolveGameEvent_NotFound() {
        // Arrange
        when(gameEventRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userGameEventService.resolveGameEvent(1L));
        assertTrue(exception.getMessage().contains("GameEvent not found"));
    }
}
