package com.example.quiz.unitTest.service.admin;

import com.example.quiz.model.entity.QuizState;
import com.example.quiz.repository.QuizStateRepository;
import com.example.quiz.service.admin.AdminQuizStateService;
import com.example.quiz.service.user.UserQuizStateService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class AdminQuizStateServiceTest {

    @Mock
    private QuizStateRepository quizStateRepository;

    @Mock
    private UserQuizStateService userQuizStateService;

    @InjectMocks
    private AdminQuizStateService adminQuizStateService;

    private QuizState dummyQuizState;

    @BeforeEach
    void setUp() {
        dummyQuizState = new QuizState(1L);
    }

    @Test
    void forceCompleteLatestQuizForUser_success() {
        when(userQuizStateService.getLatestQuizStateByUserId(dummyQuizState.getUserId()))
                .thenReturn(Optional.of(dummyQuizState));
        when(quizStateRepository.save(dummyQuizState)).thenReturn(dummyQuizState);

        QuizState result = adminQuizStateService.forceCompleteLatestQuizForUser(dummyQuizState.getUserId());
        assertNotNull(result);
        verify(quizStateRepository).save(dummyQuizState);
    }

    @Test
    void forceCompleteLatestQuizForUser_noQuizStateFound() {
        when(userQuizStateService.getLatestQuizStateByUserId(dummyQuizState.getUserId()))
                .thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> adminQuizStateService.forceCompleteLatestQuizForUser(dummyQuizState.getUserId()));
        assertTrue(ex.getMessage().contains("No quiz state found"));
    }

    @Test
    void createEmptyQuizState_success() {
        when(quizStateRepository.save(any(QuizState.class))).thenReturn(dummyQuizState);
        QuizState createdState = adminQuizStateService.createEmptyQuizState(dummyQuizState.getUserId());
        assertNotNull(createdState);
        verify(quizStateRepository).save(any(QuizState.class));
    }

}