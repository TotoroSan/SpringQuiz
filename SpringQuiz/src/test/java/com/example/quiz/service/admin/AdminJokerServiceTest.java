package com.example.quiz.service.admin;

import com.example.quiz.model.entity.GameEvent;
import com.example.quiz.model.entity.Joker.FiftyFiftyJoker;
import com.example.quiz.model.entity.Joker.Joker;
import com.example.quiz.model.entity.Joker.SkipQuestionJoker;
import com.example.quiz.model.entity.Joker.TwentyFiveSeventyFiveJoker;
import com.example.quiz.model.entity.QuestionGameEvent;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.service.user.UserJokerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminJokerServiceTest {

    @Mock
    private UserJokerService userJokerService;

    @InjectMocks
    private AdminJokerService adminJokerService;

    private QuizState dummyQuizState;

    @BeforeEach
    void setUp() {
        dummyQuizState = new QuizState(1L);
        GameEvent questionEvent = mock(QuestionGameEvent.class);
        dummyQuizState.addGameEvent(questionEvent);
    }

    @Test
    void successfullyApplyFiftyFiftyJoker() {
        String jokerIdString = "fiftyfifty";
        Integer tier = 1;

        FiftyFiftyJoker mockJoker = mock(FiftyFiftyJoker.class);

        try (MockedStatic<com.example.quiz.model.entity.Joker.JokerFactory> mockedFactory =
                     mockStatic(com.example.quiz.model.entity.Joker.JokerFactory.class)) {

            mockedFactory.when(() -> com.example.quiz.model.entity.Joker.JokerFactory.createJoker(
                    any(QuizState.class), eq(jokerIdString), eq(tier))).thenReturn(mockJoker);

            when(userJokerService.applyFiftyFiftyJoker(dummyQuizState, mockJoker)).thenReturn(true);

            boolean result = adminJokerService.applyJokerForTesting(dummyQuizState, jokerIdString, tier);

            assertTrue(result);
            verify(userJokerService).applyFiftyFiftyJoker(dummyQuizState, mockJoker);
        }
    }

    @Test
    void successfullyApplyTwentyFiveSeventyFiveJoker() {
        String jokerIdString = "twentyfiveseventyfive";
        Integer tier = 1;

        TwentyFiveSeventyFiveJoker mockJoker = mock(TwentyFiveSeventyFiveJoker.class);

        try (MockedStatic<com.example.quiz.model.entity.Joker.JokerFactory> mockedFactory =
                     mockStatic(com.example.quiz.model.entity.Joker.JokerFactory.class)) {

            mockedFactory.when(() -> com.example.quiz.model.entity.Joker.JokerFactory.createJoker(
                    any(QuizState.class), eq(jokerIdString), eq(tier))).thenReturn(mockJoker);

            when(userJokerService.applyTwentyFiveSeventyFiveJoker(dummyQuizState, mockJoker)).thenReturn(true);

            boolean result = adminJokerService.applyJokerForTesting(dummyQuizState, jokerIdString, tier);

            assertTrue(result);
            verify(userJokerService).applyTwentyFiveSeventyFiveJoker(dummyQuizState, mockJoker);
        }
    }

    @Test
    void successfullyApplySkipQuestionJoker() {
        String jokerIdString = "skipquestion";
        Integer tier = 1;

        SkipQuestionJoker mockJoker = mock(SkipQuestionJoker.class);

        try (MockedStatic<com.example.quiz.model.entity.Joker.JokerFactory> mockedFactory =
                     mockStatic(com.example.quiz.model.entity.Joker.JokerFactory.class)) {

            mockedFactory.when(() -> com.example.quiz.model.entity.Joker.JokerFactory.createJoker(
                    any(QuizState.class), eq(jokerIdString), eq(tier))).thenReturn(mockJoker);

            when(userJokerService.applySkipQuestionJoker(dummyQuizState, mockJoker)).thenReturn(true);

            boolean result = adminJokerService.applyJokerForTesting(dummyQuizState, jokerIdString, tier);

            assertTrue(result);
            verify(userJokerService).applySkipQuestionJoker(dummyQuizState, mockJoker);
        }
    }

    @Test
    void failWhenJokerTypeIsUnknown() {
        String unknownJokerType = "unknownJokerType";
        Integer tier = 1;

        try (MockedStatic<com.example.quiz.model.entity.Joker.JokerFactory> mockedFactory =
                     mockStatic(com.example.quiz.model.entity.Joker.JokerFactory.class)) {

            Joker mockJoker = mock(Joker.class);
            mockedFactory.when(() -> com.example.quiz.model.entity.Joker.JokerFactory.createJoker(
                    any(QuizState.class), eq(unknownJokerType), eq(tier))).thenReturn(mockJoker);

            boolean result = adminJokerService.applyJokerForTesting(dummyQuizState, unknownJokerType, tier);

            assertFalse(result);
            verifyNoInteractions(userJokerService);
        }
    }

    @Test
    void failWhenJokerCreationFails() {
        String jokerIdString = "fiftyfifty";
        Integer tier = 1;

        try (MockedStatic<com.example.quiz.model.entity.Joker.JokerFactory> mockedFactory =
                     mockStatic(com.example.quiz.model.entity.Joker.JokerFactory.class)) {

            mockedFactory.when(() -> com.example.quiz.model.entity.Joker.JokerFactory.createJoker(
                    any(QuizState.class), eq(jokerIdString), eq(tier))).thenReturn(null);

            boolean result = adminJokerService.applyJokerForTesting(dummyQuizState, jokerIdString, tier);

            assertFalse(result);
            verifyNoInteractions(userJokerService);
        }
    }

    @Test
    void failWhenJokerApplicationFails() {
        String jokerIdString = "fiftyfifty";
        Integer tier = 1;

        FiftyFiftyJoker mockJoker = mock(FiftyFiftyJoker.class);

        try (MockedStatic<com.example.quiz.model.entity.Joker.JokerFactory> mockedFactory =
                     mockStatic(com.example.quiz.model.entity.Joker.JokerFactory.class)) {

            mockedFactory.when(() -> com.example.quiz.model.entity.Joker.JokerFactory.createJoker(
                    any(QuizState.class), eq(jokerIdString), eq(tier))).thenReturn(mockJoker);

            when(userJokerService.applyFiftyFiftyJoker(dummyQuizState, mockJoker)).thenReturn(false);

            boolean result = adminJokerService.applyJokerForTesting(dummyQuizState, jokerIdString, tier);

            assertFalse(result);
            verify(userJokerService).applyFiftyFiftyJoker(dummyQuizState, mockJoker);
        }
    }

    @Test
    void caseInsensitiveJokerTypeMatching() {
        String jokerIdString = "FiFtYfIfTy";  // Mixed case
        Integer tier = 1;

        FiftyFiftyJoker mockJoker = mock(FiftyFiftyJoker.class);

        try (MockedStatic<com.example.quiz.model.entity.Joker.JokerFactory> mockedFactory =
                     mockStatic(com.example.quiz.model.entity.Joker.JokerFactory.class)) {

            mockedFactory.when(() -> com.example.quiz.model.entity.Joker.JokerFactory.createJoker(
                    any(QuizState.class), eq(jokerIdString), eq(tier))).thenReturn(mockJoker);

            when(userJokerService.applyFiftyFiftyJoker(dummyQuizState, mockJoker)).thenReturn(true);

            boolean result = adminJokerService.applyJokerForTesting(dummyQuizState, jokerIdString, tier);

            assertTrue(result);
            verify(userJokerService).applyFiftyFiftyJoker(dummyQuizState, mockJoker);
        }
    }
}