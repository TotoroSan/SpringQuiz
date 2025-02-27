// Sprache: java
package com.example.quiz.service.admin;

import com.example.quiz.model.dto.JokerDto;
import com.example.quiz.model.entity.GameEvent;
import com.example.quiz.model.entity.Joker.FiftyFiftyJoker;
import com.example.quiz.model.entity.Joker.Joker;
import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.repository.QuizStateRepository;
import com.example.quiz.service.user.UserGameEventService;
import com.example.quiz.service.user.UserJokerService;
import com.example.quiz.service.user.UserQuizStateService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminGameEventServiceTest {

    @Mock
    private QuizStateRepository quizStateRepository;

    @Mock
    private UserGameEventService userGameEventService;

    @Mock
    private UserJokerService userJokerService;

    @Mock
    private UserQuizStateService userQuizStateService;

    @InjectMocks
    private QuizState dummyQuizState;
    private JokerDto dummyJokerDto;

    @BeforeEach
    void setUp() {
        // Set security context with admin authority
        TestingAuthenticationToken auth =
                new TestingAuthenticationToken("admin", "password", "ROLE_ADMIN");
        auth.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(auth);

        dummyQuizState = new QuizState(1L);
        // Instead of direct instantiation, use a Mockito mock for JokerDto.
        dummyJokerDto = mock(JokerDto.class);
        when(dummyJokerDto.getIdString()).thenReturn("fiftyFifty");
        when(dummyJokerDto.getTier()).thenReturn(1);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }





    @Test
    void testAddJokerToQuizState_success() {
        try (MockedStatic<com.example.quiz.model.entity.Joker.JokerFactory> jokerFactoryMock =
                     mockStatic(com.example.quiz.model.entity.Joker.JokerFactory.class)) {

            // Create a dummy joker instance (FiftyFiftyJoker) via mocking
            FiftyFiftyJoker dummyJoker = mock(FiftyFiftyJoker.class);
            UUID jokerId = UUID.randomUUID();
            when(dummyJoker.getId()).thenReturn(jokerId);

            jokerFactoryMock.when(() ->
                            com.example.quiz.model.entity.Joker.JokerFactory.createJoker(dummyQuizState,
                                    dummyJokerDto.getIdString(), dummyJokerDto.getTier()))
                    .thenReturn(dummyJoker);
            // Convert the created Joker to a DTO using the service.
            when(userJokerService.convertToDto(dummyJoker)).thenReturn(dummyJokerDto);
            when(quizStateRepository.save(dummyQuizState)).thenReturn(dummyQuizState);

            JokerDto result = adminGameEventService.addJokerToQuizState(dummyQuizState, dummyJokerDto);
            assertEquals(dummyJokerDto, result);
            verify(quizStateRepository, times(1)).save(dummyQuizState);
        }
    }

    @Test
    void testRemoveJokerFromQuizState_success() {
        UUID jokerId = UUID.randomUUID();
        // Simulate that dummyQuizState has a joker mapped with jokerId.
        FiftyFiftyJoker dummyJoker = mock(FiftyFiftyJoker.class);
        dummyQuizState.getOwnedJokers().put(jokerId, dummyJoker);
        when(quizStateRepository.save(dummyQuizState)).thenReturn(dummyQuizState);

        assertDoesNotThrow(() -> adminGameEventService.removeJokerFromQuizState(dummyQuizState, jokerId));
        verify(quizStateRepository, times(1)).save(dummyQuizState);
    }

    @Test
    void testRemoveJokerFromQuizState_notFound() {
        UUID jokerId = UUID.randomUUID();
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> adminGameEventService.removeJokerFromQuizState(dummyQuizState, jokerId));
        assertTrue(ex.getMessage().contains("Joker with given ID does not exist"));
    }

    @Test
    void testModifyQuizModifier_success() {
        QuizModifier existingModifier = new QuizModifier();
        existingModifier.setCash(100);
        existingModifier.setLifeCounter(3);
        existingModifier.setScoreMultiplier(1.0);
        dummyQuizState.setQuizModifier(existingModifier);

        QuizModifier updatedModifier = new QuizModifier();
        updatedModifier.setCash(200);
        updatedModifier.setLifeCounter(5);
        updatedModifier.setScoreMultiplier(2.0);

        when(quizStateRepository.save(dummyQuizState)).thenReturn(dummyQuizState);

        QuizModifier result = adminGameEventService.modifyQuizModifier(dummyQuizState, updatedModifier);
        assertEquals(200, result.getCash());
        assertEquals(5, result.getLifeCounter());
        assertEquals(2.0, result.getScoreMultiplier());
        verify(quizStateRepository, times(1)).save(dummyQuizState);
    }

    @Test
    void testTestJokerEffect_success() {
        GameEvent dummyEvent = mock(GameEvent.class);
        // Force dummyQuizState so that createQuestionGameEvent wird aufgerufen.
        dummyQuizState.setLatestGameEvent(null);
        when(userQuizStateService.createQuestionGameEvent(dummyQuizState)).thenAnswer(invocation -> {
            GameEvent newEvent = mock(GameEvent.class);
            dummyQuizState.setLatestGameEvent(newEvent);
            return newEvent;
        });

        try (MockedStatic<com.example.quiz.model.entity.Joker.JokerFactory> jokerFactoryMock =
                     mockStatic(com.example.quiz.model.entity.Joker.JokerFactory.class)) {

            FiftyFiftyJoker dummyJoker = mock(FiftyFiftyJoker.class);
            when(dummyJoker.getId()).thenReturn(UUID.randomUUID());
            jokerFactoryMock.when(() ->
                            com.example.quiz.model.entity.Joker.JokerFactory.createJoker(dummyQuizState, "fiftyFifty", 1))
                    .thenReturn(dummyJoker);
            when(userJokerService.applyFiftyFiftyJoker(dummyQuizState, dummyJoker)).thenReturn(true);
            when(quizStateRepository.save(dummyQuizState)).thenReturn(dummyQuizState);

            String result = adminGameEventService.testJokerEffect(dummyQuizState, "fiftyFifty");
            assertEquals("Joker effect applied successfully", result);
            verify(quizStateRepository, times(1)).save(dummyQuizState);
        }
    }

    @Test
    void testTestJokerEffect_failure() {
        GameEvent dummyEvent = mock(GameEvent.class);
        dummyQuizState.setLatestGameEvent(dummyEvent);

        try (MockedStatic<com.example.quiz.model.entity.Joker.JokerFactory> jokerFactoryMock =
                     mockStatic(com.example.quiz.model.entity.Joker.JokerFactory.class)) {

            FiftyFiftyJoker dummyJoker = mock(FiftyFiftyJoker.class);
            when(dummyJoker.getId()).thenReturn(UUID.randomUUID());
            jokerFactoryMock.when(() ->
                            com.example.quiz.model.entity.Joker.JokerFactory.createJoker(dummyQuizState, "fiftyFifty", 1))
                    .thenReturn(dummyJoker);
            when(userJokerService.applyFiftyFiftyJoker(dummyQuizState, dummyJoker)).thenReturn(false);

            String result = adminGameEventService.testJokerEffect(dummyQuizState, "fiftyFifty");
            assertEquals("Failed to apply joker effect", result);
        }
    }

    @Test
    void testCheckAdminPermission_insufficientRights() {
        SecurityContextHolder.clearContext();
        AccessDeniedException ex = assertThrows(AccessDeniedException.class,
                () -> adminGameEventService.triggerShopEvent(dummyQuizState));
        assertTrue(ex.getMessage().contains("Insufficient permissions"));
    }
}