// Sprache: java
package com.example.quiz.unitTest.service.admin;
import com.example.quiz.model.dto.JokerDto;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.repository.QuizStateRepository;
import com.example.quiz.service.user.UserGameEventService;
import com.example.quiz.service.user.UserJokerService;
import com.example.quiz.service.user.UserQuizStateService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
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


}