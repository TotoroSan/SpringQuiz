package com.example.quiz.controller.admin;

import com.example.quiz.model.dto.GameEventDto;
import com.example.quiz.model.dto.QuizStateDto;
import com.example.quiz.model.entity.GameEvent;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.model.entity.User;
import com.example.quiz.repository.QuizStateRepository;
import com.example.quiz.service.user.UserGameEventService;
import com.example.quiz.service.user.UserJokerService;
import com.example.quiz.service.user.UserQuizStateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Controller for administrative operations related to GameEvents.
 * Each endpoint retrieves the active quiz state of the authenticated user and processes events accordingly.
 */
@RestController
@RequestMapping("/api/admin/quiz/event")
@PreAuthorize("hasRole('ADMIN')")
public class AdminGameEventController {
    private static final Logger logger = LoggerFactory.getLogger(AdminGameEventController.class);

    private final QuizStateRepository quizStateRepository;
    private final UserQuizStateService userQuizStateService;
    private final UserGameEventService userGameEventService;
    private final UserJokerService userJokerService;

    /**
     * Constructor injecting required services and repositories.
     *
     * @param quizStateRepository   Repository for QuizState entities
     * @param userQuizStateService  Service for quiz state operations
     * @param userGameEventService  Service for game event operations
     * @param userJokerService      Service for joker operations
     */
    @Autowired
    public AdminGameEventController(QuizStateRepository quizStateRepository,
                                    UserQuizStateService userQuizStateService,
                                    UserGameEventService userGameEventService,
                                    UserJokerService userJokerService) {
        this.quizStateRepository = quizStateRepository;
        this.userQuizStateService = userQuizStateService;
        this.userGameEventService = userGameEventService;
        this.userJokerService = userJokerService;
    }

    /**
     * Forces a question event for the active quiz state of the authenticated user.
     *
     * @param user The authenticated user
     * @return A ResponseEntity containing the GameEventDto
     * @throws EntityNotFoundException if the active QuizState is not found
     */
    @Operation(summary = "Force a question event", description = "Forces a question event for the active quiz state of the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question event forced successfully"),
            @ApiResponse(responseCode = "404", description = "Active QuizState not found")
    })
    @PostMapping("/question")
    public ResponseEntity<GameEventDto> forceQuestionEvent(@AuthenticationPrincipal User user) {
        logger.info("Admin forcing question event for user ID: {}", user.getId());
        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestActiveQuizStateByUserId(user.getId());
        if (optionalQuizState.isEmpty()) {
            throw new EntityNotFoundException("Active QuizState not found for user ID: " + user.getId());
        }
        QuizState quizState = optionalQuizState.get();
        GameEvent gameEvent = userQuizStateService.createQuestionGameEvent(quizState);
        return ResponseEntity.ok(userGameEventService.convertToDto(gameEvent));
    }

    /**
     * Forces a shop event for the active quiz state of the authenticated user.
     *
     * @param user The authenticated user
     * @return A ResponseEntity containing the GameEventDto
     * @throws EntityNotFoundException if the active QuizState is not found
     */
    @Operation(summary = "Force a shop event", description = "Forces a shop event for the active quiz state of the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shop event forced successfully"),
            @ApiResponse(responseCode = "404", description = "Active QuizState not found")
    })
    @PostMapping("/shop")
    public ResponseEntity<GameEventDto> forceShopEvent(@AuthenticationPrincipal User user) {
        logger.info("Admin forcing shop event for user ID: {}", user.getId());
        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestActiveQuizStateByUserId(user.getId());
        if (optionalQuizState.isEmpty()) {
            throw new EntityNotFoundException("Active QuizState not found for user ID: " + user.getId());
        }
        QuizState quizState = optionalQuizState.get();
        GameEvent gameEvent = userQuizStateService.createShopGameEvent(quizState);
        return ResponseEntity.ok(userGameEventService.convertToDto(gameEvent));
    }

    /**
     * Forces a modifier effects event for the active quiz state of the authenticated user.
     *
     * @param user The authenticated user
     * @return A ResponseEntity containing the GameEventDto
     * @throws EntityNotFoundException if the active QuizState is not found
     */
    @Operation(summary = "Force a modifier effects event", description = "Forces a modifier event for the active quiz state of the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modifier event forced successfully"),
            @ApiResponse(responseCode = "404", description = "Active QuizState not found")
    })
    @PostMapping("/modifier")
    public ResponseEntity<GameEventDto> forceModifierEvent(@AuthenticationPrincipal User user) {
        logger.info("Admin forcing modifier event for user ID: {}", user.getId());
        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestActiveQuizStateByUserId(user.getId());
        if (optionalQuizState.isEmpty()) {
            throw new EntityNotFoundException("Active QuizState not found for user ID: " + user.getId());
        }
        QuizState quizState = optionalQuizState.get();
        GameEvent gameEvent = userQuizStateService.createModifierEffectsGameEvent(quizState);
        return ResponseEntity.ok(userGameEventService.convertToDto(gameEvent));
    }

    /**
     * Tests the skip question event for the active quiz state of the authenticated user.
     *
     * @param user The authenticated user
     * @return A ResponseEntity containing the QuizStateDto after processing the skip event
     * @throws EntityNotFoundException if the active QuizState is not found
     */
    @Operation(summary = "Test skip question", description = "Tests the skip question event for the active quiz state of the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Skip question processed successfully"),
            @ApiResponse(responseCode = "404", description = "Active QuizState not found")
    })
    @PostMapping("/skip")
    public ResponseEntity<QuizStateDto> testSkipQuestion(@AuthenticationPrincipal User user) {
        logger.info("Admin testing skip question for user ID: {}", user.getId());
        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestActiveQuizStateByUserId(user.getId());
        if (optionalQuizState.isEmpty()) {
            throw new EntityNotFoundException("Active QuizState not found for user ID: " + user.getId());
        }
        QuizState quizState = optionalQuizState.get();
        userQuizStateService.processSkipQuestionSubmission(quizState);
        return ResponseEntity.ok(userQuizStateService.convertToDto(quizState));
    }



}