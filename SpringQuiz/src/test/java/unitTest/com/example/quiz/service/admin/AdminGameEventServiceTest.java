// Sprache: java
package unitTest.com.example.quiz.service.admin;

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


}