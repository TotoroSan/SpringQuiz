package com.example.quiz.service.user;

import com.example.quiz.model.dto.QuizModifierDto;
import com.example.quiz.model.dto.QuizModifierEffectDto;
import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffectFactory;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffectMetaData;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.repository.GameEventRepository;
import com.example.quiz.repository.QuizModifierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserQuizModifierServiceTest {

    @InjectMocks
    private UserQuizModifierService userQuizModifierService;

    @Mock
    private QuizModifierRepository quizModifierRepository;

    @Mock
    private GameEventRepository gameEventRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPickRandomModifierEffectDtos() {
        // Arrange
        QuizModifierEffectMetaData metaData1 = new QuizModifierEffectMetaData("effect1", "Effect 1", "Test Description 1", 10, "type", false, 1, 50);
        QuizModifierEffectMetaData metaData2 = new QuizModifierEffectMetaData("effect2", "Effect 2", "Test Description 2", 20, "type", false, 2, 50);

        Map<String, QuizModifierEffectMetaData> registry = new HashMap<>();
        registry.put(metaData1.getIdString(), metaData1);
        registry.put(metaData2.getIdString(), metaData2);

        //QuizModifierEffectFactory.setQuizModifierEffectMetadataRegistry(registry);

        when(QuizModifierEffectFactory.rollTier()).thenReturn(1);

        // Act
        List<QuizModifierEffectDto> result = userQuizModifierService.pickRandomModifierEffectDtos();

        // Assert
        assertNotNull(result);
        assertTrue(result.size() <= 3);
    }

    @Test
    void testApplyModifierEffectByIdString() {
        // Arrange
        QuizState quizState = new QuizState();
        QuizModifier quizModifier = quizState.getQuizModifier();
        String effectId = "test-effect";
        QuizModifierEffect effectMock = mock(QuizModifierEffect.class);

        when(QuizModifierEffectFactory.createEffect(eq(effectId), anyInt(), eq(quizModifier), anyInt())).thenReturn(effectMock);

        // Act
        boolean result = userQuizModifierService.applyModifierEffectByIdString(quizModifier, effectId, 3, 1);

        // Assert
        assertTrue(result);
        verify(effectMock, times(1)).apply(quizModifier);
    }

    @Test
    void testConvertToDto_NonTopicEffect() {
        // Arrange
        QuizModifierEffectMetaData metaData = new QuizModifierEffectMetaData("effect1", "Effect 1", "Test Description", 10, "type", false, 1, 50);

        Map<String, QuizModifierEffectMetaData> registry = new HashMap<>();
        registry.put(metaData.getIdString(), metaData);

        //QuizModifierEffectFactory.setQuizModifierEffectMetadataRegistry(registry);

        UUID uuid = UUID.randomUUID();

        // Act
        QuizModifierEffectDto result = userQuizModifierService.convertToDto(uuid, "effect1", 1, 5);

        // Assert
        assertNotNull(result);
        assertEquals(metaData.getIdString(), result.getIdString());
        assertEquals(metaData.getName(), result.getName());
    }

    @Test
    void testConvertToDto_TopicEffect() {
        // Arrange
        QuizModifierEffectMetaData metaData = new QuizModifierEffectMetaData("CHOOSE_TOPIC", "Choose Topic", "Choose a topic", 10, "type", false, 1, 50);

        Map<String, QuizModifierEffectMetaData> registry = new HashMap<>();
        registry.put("CHOOSE_TOPIC", metaData);

        //QuizModifierEffectFactory.setQuizModifierEffectMetadataRegistry(registry);

        UUID uuid = UUID.randomUUID();

        // Act
        QuizModifierEffectDto result = userQuizModifierService.convertToDto(uuid, "CHOOSE_TOPIC_MATH", 1, 5);

        // Assert
        assertNotNull(result);
        assertEquals("CHOOSE_TOPIC_MATH", result.getIdString());
        assertEquals(metaData.getName(), result.getName());
        assertTrue(result.getDescription().contains("Math"));
    }

    @Test
    void testProcessActiveQuizModifierEffectsForNewRound() {
        // Arrange
        QuizState quizState = new QuizState();
        QuizModifier quizModifier = quizState.getQuizModifier();
        QuizModifierEffect activeEffect = mock(QuizModifierEffect.class);
        when(activeEffect.getDuration()).thenReturn(1);
        when(activeEffect.getPermanent()).thenReturn(false);

        quizModifier.addActiveQuizModifierEffect(activeEffect);

        // Act
        userQuizModifierService.processActiveQuizModifierEffectsForNewRound(quizModifier);

        // Assert
        verify(activeEffect, times(1)).decrementDuration();
        verify(activeEffect, never()).reverse(quizModifier);
    }

    @Test
    void testConvertToQuizModifierDto() {
        // Arrange
        QuizState quizState = new QuizState();
        QuizModifier quizModifier = quizState.getQuizModifier();
        quizModifier.setId(1L);
        quizModifier.setCash(100);
        quizModifier.setLifeCounter(3);

        QuizModifierEffect activeEffect = mock(QuizModifierEffect.class);
        quizModifier.addActiveQuizModifierEffect(activeEffect);

        QuizModifierEffectDto effectDto = new QuizModifierEffectDto(UUID.randomUUID(), "effect1", "Effect 1", 5, "Description", "type", false, 1, 1);
        when(userQuizModifierService.convertToDto(activeEffect)).thenReturn(effectDto);

        // Act
        QuizModifierDto result = userQuizModifierService.convertToDto(quizModifier);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getActiveQuizModifierEffectDtos().size());
        assertEquals(3, result.getLifeCounter());
        assertEquals(100, result.getCash());
    }
}
