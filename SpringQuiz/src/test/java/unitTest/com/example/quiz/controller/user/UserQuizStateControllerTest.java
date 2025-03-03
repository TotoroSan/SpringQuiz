package unitTest.com.example.quiz.controller.user;

import com.example.quiz.model.dto.*;
import com.example.quiz.model.entity.*;
import com.example.quiz.service.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpSession;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserQuizStateControllerTest {

    @InjectMocks
    private UserQuizStateController userQuizStateController;

    @Mock
    private UserQuizStateService userQuizStateService;

    @Mock
    private UserQuizModifierService userQuizModifierService;

    @Mock
    private UserQuestionService userQuestionService;

    @Mock
    private UserGameEventService userGameEventService;

    @Mock
    private UserJokerService userJokerService;

    private User user;
    private QuizState quizState;
    private JokerDto jokerDto;
    private UUID jokerUuid;

    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        quizState = mock(QuizState.class);
        jokerUuid = UUID.randomUUID();

        jokerDto = new JokerDto(jokerUuid, "FIFTY_FIFTY", "50/50 Joker", "Removes two wrong answers", 50, 1, 2, 1);

        when(user.getId()).thenReturn(1L);
        when(userQuizStateService.getLatestQuizStateByUserId(1L)).thenReturn(Optional.of(quizState));
    }

    @Test
    void testStartQuiz() {
        // Arrange
        User user = new User();
        user.setId(1L);
        QuizState quizState = new QuizState();
        when(userQuizStateService.startNewQuiz(1L)).thenReturn(quizState);

        // Act
        ResponseEntity<String> response = userQuizStateController.startQuiz(session, user);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Quiz started!", response.getBody());
        verify(session, times(1)).setAttribute("quizState", quizState);
    }

    @Test
    void testLoadLastActiveQuiz_Found() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1L);
        QuizState quizState = new QuizState();
        QuizSaveDto quizSaveDto = new QuizSaveDto();
        when(userQuizStateService.getLatestActiveQuizStateByUserId(1L)).thenReturn(Optional.of(quizState));
        when(userQuizStateService.createQuizSaveDto(quizState)).thenReturn(quizSaveDto);

        // Act
        ResponseEntity<QuizSaveDto> response = userQuizStateController.loadLastActiveQuiz(session, user);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(quizSaveDto, response.getBody());
        verify(session, times(1)).setAttribute("quizState", quizState);
    }

    @Test
    void testLoadLastActiveQuiz_NotFound() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1L);
        when(userQuizStateService.getLatestActiveQuizStateByUserId(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<QuizSaveDto> response = userQuizStateController.loadLastActiveQuiz(session, user);

        // Assert
        assertEquals(204, response.getStatusCodeValue());
        verify(session, never()).setAttribute(eq("quizState"), any());
    }

    @Test
    void testGetQuizState() {
        // Arrange
        User user = new User();
        user.setId(1L);
        QuizState quizState = new QuizState();
        QuizStateDto quizStateDto = new QuizStateDto();
        when(userQuizStateService.getLatestQuizStateByUserId(1L)).thenReturn(Optional.of(quizState));
        when(userQuizStateService.convertToDto(quizState)).thenReturn(quizStateDto);

        // Act
        ResponseEntity<QuizStateDto> response = userQuizStateController.getQuizState(session, user);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(quizStateDto, response.getBody());
        verify(session, times(1)).setAttribute("quizState", quizState);
    }

    @Test
    void testGetNextGameEvent() {
        // Arrange
        User user = new User();
        user.setId(1L);
        QuizState quizState = new QuizState();
        quizState.setActive(true);
        GameEvent gameEvent = mock(GameEvent.class);
        GameEventDto gameEventDto = mock(GameEventDto.class);
        when(userQuizStateService.getLatestQuizStateByUserId(1L)).thenReturn(Optional.of(quizState));
        when(userQuizStateService.getNextGameEvent(quizState)).thenReturn(gameEvent);
        when(userGameEventService.convertToDto(gameEvent)).thenReturn(gameEventDto);

        // Act
        ResponseEntity<GameEventDto> response = userQuizStateController.getNextGameEvent(session, user);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(gameEventDto, response.getBody());
        verify(session, times(1)).setAttribute("quizState", quizState);
    }

    @Test
    void testApplyModifier_Success() {
        // Arrange
        User user = new User();
        user.setId(1L);
        QuizModifierEffectDto effectDto = new QuizModifierEffectDto(UUID.randomUUID(), "effect1", "Effect 1", 3, "Description", "type", false, 1, 1);

        QuizState quizState = new QuizState();
        when(userQuizStateService.getLatestQuizStateByUserId(1L)).thenReturn(Optional.of(quizState));
        when(userQuizStateService.validateAndApplyModifierEffect(quizState, effectDto.getUuid())).thenReturn(true);

        // Act
        ResponseEntity<String> response = userQuizStateController.applyModifier(session, effectDto, user);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Modifier applied successfully", response.getBody());
        verify(session, times(1)).setAttribute("quizState", quizState);
    }

    @Test
    void testGetActiveQuizModifierDtos() {
        // Arrange
        User user = new User();
        user.setId(1L);
        QuizState quizState = new QuizState();
        QuizModifier quizModifier = quizState.getQuizModifier();
        quizState.setQuizModifier(quizModifier);
        QuizModifierEffectDto effectDto = new QuizModifierEffectDto(UUID.randomUUID(), "effect1", "Effect 1", 3, "Description", "type", false, 1, 1);

        when(userQuizStateService.getLatestQuizStateByUserId(1L)).thenReturn(Optional.of(quizState));
        when(userQuizModifierService.getActiveModifierEffectDtos(quizModifier)).thenReturn(List.of(effectDto));

        // Act
        ResponseEntity<List<QuizModifierEffectDto>> response = userQuizStateController.getActiveQuizModifierDtos(user);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(effectDto, response.getBody().get(0));
    }


    @Test
    void testPurchaseJoker_Successful() {
        when(userJokerService.purchaseJoker(quizState, jokerDto.getIdString(), jokerDto.getTier())).thenReturn(true);

        ResponseEntity<String> response = userQuizStateController.purchaseJoker(user, session, jokerDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Joker purchased successfully!", response.getBody());
        verify(session, times(1)).setAttribute("quizState", quizState);
    }

    @Test
    void testPurchaseJoker_Fails_NoQuizState() {
        when(userQuizStateService.getLatestQuizStateByUserId(1L)).thenReturn(Optional.empty());

        ResponseEntity<String> response = userQuizStateController.purchaseJoker(user, session, jokerDto);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("No active quiz state found.", response.getBody());
    }

    @Test
    void testPurchaseJoker_Fails_InvalidJoker() {
        when(userJokerService.purchaseJoker(quizState, jokerDto.getIdString(), jokerDto.getTier())).thenReturn(false);

        ResponseEntity<String> response = userQuizStateController.purchaseJoker(user, session, jokerDto);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Could not purchase Joker (invalid ID, insufficient funds, or no shop event).", response.getBody());
    }

    @Test
    void testUseJoker_Successful() {
        when(userJokerService.useJoker(quizState, jokerUuid)).thenReturn(true);

        ResponseEntity<String> response = userQuizStateController.useJoker(session, user, jokerDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Joker used successfully!", response.getBody());
        verify(session, times(1)).setAttribute("quizState", quizState);
    }

    @Test
    void testUseJoker_Fails_NoQuizState() {
        when(userQuizStateService.getLatestQuizStateByUserId(1L)).thenReturn(Optional.empty());

        ResponseEntity<String> response = userQuizStateController.useJoker(session, user, jokerDto);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("No active quiz state found.", response.getBody());
    }

    @Test
    void testUseJoker_Fails_InvalidJoker() {
        when(userJokerService.useJoker(quizState, jokerUuid)).thenReturn(false);

        ResponseEntity<String> response = userQuizStateController.useJoker(session, user, jokerDto);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Failed to use Joker (invalid ID or no uses left).", response.getBody());
    }

    @Test
    void testGetOwnedJokers_Successful() {
        List<JokerDto> activeJokers = Arrays.asList(jokerDto);
        when(userJokerService.getOwnedJokerDtos(quizState)).thenReturn(activeJokers);

        ResponseEntity<List<JokerDto>> response = userQuizStateController.getOwnedJokers(user);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("FIFTY_FIFTY", response.getBody().get(0).getIdString());
    }

    @Test
    void testGetOwnedJokers_Fails_NoQuizState() {
        when(userQuizStateService.getLatestQuizStateByUserId(1L)).thenReturn(Optional.empty());

        ResponseEntity<List<JokerDto>> response = userQuizStateController.getOwnedJokers(user);

        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
}
