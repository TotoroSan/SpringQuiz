package com.example.quiz.controller.admin;

import com.example.quiz.model.dto.JokerDto;
import com.example.quiz.model.dto.QuizStateDto;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.model.entity.User;
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
class AdminQuizStateControllerTest {

    @Mock
    private UserQuizStateService userQuizStateService;

    @InjectMocks
    private AdminQuizStateController controller;

    private User dummyUser;
    private QuizState dummyQuizState;
    private QuizStateDto dummyQuizStateDto;

    @BeforeEach
    void setUp() {
        dummyUser = new User();
        dummyUser.setId(2L);

        dummyQuizState = new QuizState(dummyUser.getId());
        dummyQuizStateDto = new QuizStateDto();
    }

    @Test
    void resetQuizState_success() {
        when(userQuizStateService.getLatestActiveQuizStateByUserId(dummyUser.getId()))
                .thenReturn(Optional.of(dummyQuizState));
        when(userQuizStateService.convertToDto(dummyQuizState)).thenReturn(dummyQuizStateDto);
        // Assuming processQuizEnd doesn't call more services
        doNothing().when(userQuizStateService).processQuizEnd(dummyQuizState);
        // assuming startnewquiz returns the same quiz state for simplicties sake
        when(userQuizStateService.startNewQuiz(dummyQuizState.getUserId()))
                .thenReturn(dummyQuizState);

        ResponseEntity<QuizStateDto> response = controller.resetQuizState(dummyUser);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dummyQuizStateDto, response.getBody());
    }

    @Test
    void resetQuizState_noActiveQuizState() {
        when(userQuizStateService.getLatestActiveQuizStateByUserId(dummyUser.getId()))
                .thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> controller.resetQuizState(dummyUser));
        assertTrue(ex.getMessage().contains("Active QuizState not found"));
    }


}