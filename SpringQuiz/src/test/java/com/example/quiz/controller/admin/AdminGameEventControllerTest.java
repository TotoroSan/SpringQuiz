// Sprache: java
package com.example.quiz.controller.admin;

import com.example.quiz.model.dto.GameEventDto;
import com.example.quiz.model.dto.JokerDto;
import com.example.quiz.model.dto.QuizStateDto;
import com.example.quiz.model.entity.*;
import com.example.quiz.service.user.UserGameEventService;
import com.example.quiz.service.user.UserJokerService;
import com.example.quiz.service.user.UserQuizStateService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminGameEventControllerTest {

    @Mock
    private UserQuizStateService userQuizStateService;

    @Mock
    private UserGameEventService userGameEventService;

    @Mock
    @InjectMocks
    private AdminGameEventController controller;

    private User dummyUser;
    private QuizState dummyQuizState;
    private GameEvent dummyGameEvent;
    private GameEventDto dummyGameEventDto;
    private QuizStateDto dummyQuizStateDto;
    private JokerDto dummyJokerDto;

    @BeforeEach
    void setUp() {
        dummyUser = new User();
        dummyUser.setId(1L);

        // Verwende nun eine Unterklasse von GameEvent
        dummyQuizState = new QuizState(dummyUser.getId());
        dummyGameEvent = new TestGameEvent();
        dummyGameEventDto = mock(GameEventDto.class);
        dummyQuizStateDto = new QuizStateDto();

        dummyJokerDto = mock(JokerDto.class);
        when(dummyJokerDto.getIdString()).thenReturn("fiftyFifty");
        when(dummyJokerDto.getTier()).thenReturn(1);
    }

    @Test
    void forceQuestionEvent_success() {
        when(userQuizStateService.getLatestActiveQuizStateByUserId(dummyUser.getId()))
                .thenReturn(Optional.of(dummyQuizState));
        when(userQuizStateService.createQuestionGameEvent(dummyQuizState))
                .thenReturn((QuestionGameEvent) dummyGameEvent);
        when(userGameEventService.convertToDto(dummyGameEvent))
                .thenReturn(dummyGameEventDto);

        ResponseEntity<GameEventDto> response = controller.forceQuestionEvent(dummyUser);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dummyGameEventDto, response.getBody());

        verify(userQuizStateService).getLatestActiveQuizStateByUserId(dummyUser.getId());
        verify(userQuizStateService).createQuestionGameEvent(dummyQuizState);
        verify(userGameEventService).convertToDto(dummyGameEvent);
    }

    @Test
    void forceQuestionEvent_noQuizStateFound() {
        when(userQuizStateService.getLatestActiveQuizStateByUserId(dummyUser.getId()))
                .thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> controller.forceQuestionEvent(dummyUser));
        assertTrue(ex.getMessage().contains("Active QuizState not found"));
    }

    @Test
    void forceShopEvent_success() {
        when(userQuizStateService.getLatestActiveQuizStateByUserId(dummyUser.getId()))
                .thenReturn(Optional.of(dummyQuizState));
        when(userQuizStateService.createShopGameEvent(dummyQuizState))
                .thenReturn((ShopGameEvent) dummyGameEvent);
        when(userGameEventService.convertToDto(dummyGameEvent))
                .thenReturn(dummyGameEventDto);

        ResponseEntity<GameEventDto> response = controller.forceShopEvent(dummyUser);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dummyGameEventDto, response.getBody());

        verify(userQuizStateService).getLatestActiveQuizStateByUserId(dummyUser.getId());
        verify(userQuizStateService).createShopGameEvent(dummyQuizState);
        verify(userGameEventService).convertToDto(dummyGameEvent);
    }

    @Test
    void forceModifierEvent_success() {
        when(userQuizStateService.getLatestActiveQuizStateByUserId(dummyUser.getId()))
                .thenReturn(Optional.of(dummyQuizState));
        when(userQuizStateService.createModifierEffectsGameEvent(dummyQuizState))
                .thenReturn((ModifierEffectsGameEvent) dummyGameEvent);
        when(userGameEventService.convertToDto(dummyGameEvent))
                .thenReturn(dummyGameEventDto);

        ResponseEntity<GameEventDto> response = controller.forceModifierEvent(dummyUser);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dummyGameEventDto, response.getBody());

        verify(userQuizStateService).getLatestActiveQuizStateByUserId(dummyUser.getId());
        verify(userQuizStateService).createModifierEffectsGameEvent(dummyQuizState);
        verify(userGameEventService).convertToDto(dummyGameEvent);
    }

    @Test
    void testSkipQuestion_success() {
        when(userQuizStateService.getLatestActiveQuizStateByUserId(dummyUser.getId()))
                .thenReturn(Optional.of(dummyQuizState));
        doNothing().when(userQuizStateService).processSkipQuestionSubmission(dummyQuizState);
        when(userQuizStateService.convertToDto(dummyQuizState))
                .thenReturn(dummyQuizStateDto);

        ResponseEntity<QuizStateDto> response = controller.testSkipQuestion(dummyUser);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dummyQuizStateDto, response.getBody());

        verify(userQuizStateService).getLatestActiveQuizStateByUserId(dummyUser.getId());
        verify(userQuizStateService).processSkipQuestionSubmission(dummyQuizState);
        verify(userQuizStateService).convertToDto(dummyQuizState);
    }

    @Test
    void testSkipQuestion_noQuizStateFound() {
        when(userQuizStateService.getLatestActiveQuizStateByUserId(dummyUser.getId()))
                .thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> controller.testSkipQuestion(dummyUser));
        assertTrue(ex.getMessage().contains("Active QuizState not found"));
    }



    // Dummy subclass of GameEvent used for tests.
    private static class TestGameEvent extends GameEvent {
        // Leere Implementierung für Testzwecke.
    }
}