package com.example.quiz.controller.admin;

import com.example.quiz.model.dto.GameEventDto;
import com.example.quiz.model.dto.JokerDto;
import com.example.quiz.model.dto.QuizStateDto;
import com.example.quiz.model.entity.GameEvent;
import com.example.quiz.model.entity.QuizState;
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
import org.springframework.web.bind.annotation.*;

/**
        * Controller for administrative actions related to game events.
        * Provides endpoints for testing and troubleshooting game events.
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
     * Forces a question event for the given quiz state.
     *
     * @param quizStateId the ID of the quiz state
     * @return the created game event DTO
     */
    @Operation(summary = "Force a question event", description = "Forces a question event for the specified quiz state id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question event forced successfully"),
            @ApiResponse(responseCode = "404", description = "QuizState not found")
    })
    @PostMapping("/{quizStateId}/question")
    public ResponseEntity<GameEventDto> forceQuestionEvent(@PathVariable Long quizStateId) {
        logger.info("Admin forcing question event for quiz state ID: {}", quizStateId);
        QuizState quizState = getQuizState(quizStateId);
        GameEvent gameEvent = userQuizStateService.createQuestionGameEvent(quizState);
        return ResponseEntity.ok(userGameEventService.convertToDto(gameEvent));
    }

    /**
     * Forces a shop event for the given quiz state.
     *
     * @param quizStateId the ID of the quiz state
     * @return the created game event DTO
     */
    @Operation(summary = "Force a shop event", description = "Forces a shop event for the specified quiz state id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shop event forced successfully"),
            @ApiResponse(responseCode = "404", description = "QuizState not found")
    })
    @PostMapping("/{quizStateId}/shop")
    public ResponseEntity<GameEventDto> forceShopEvent(@PathVariable Long quizStateId) {
        logger.info("Admin forcing shop event for quiz state ID: {}", quizStateId);
        QuizState quizState = getQuizState(quizStateId);
        GameEvent gameEvent = userQuizStateService.createShopGameEvent(quizState);
        return ResponseEntity.ok(userGameEventService.convertToDto(gameEvent));
    }

    /**
     * Forces a modifier event for the given quiz state.
     *
     * @param quizStateId the ID of the quiz state
     * @return the created game event DTO
     */
    @Operation(summary = "Force a modifier effects event", description = "Forces a modifier event for the specified quiz state id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modifier event forced successfully"),
            @ApiResponse(responseCode = "404", description = "QuizState not found")
    })
    @PostMapping("/{quizStateId}/modifier")
    public ResponseEntity<GameEventDto> forceModifierEvent(@PathVariable Long quizStateId) {
        logger.info("Admin forcing modifier effects event for quiz state ID: {}", quizStateId);
        QuizState quizState = getQuizState(quizStateId);
        GameEvent gameEvent = userQuizStateService.createModifierEffectsGameEvent(quizState);
        return ResponseEntity.ok(userGameEventService.convertToDto(gameEvent));
    }

    /**
     * Tests skipping a question for the given quiz state.
     *
     * @param quizStateId the ID of the quiz state
     * @return the updated quiz state DTO after processing the skip
     */
    @Operation(summary = "Test skip question", description = "Tests the skip question event for the specified quiz state id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Skip question processed successfully"),
            @ApiResponse(responseCode = "404", description = "QuizState not found")
    })
    @PostMapping("/{quizStateId}/skip")
    public ResponseEntity<QuizStateDto> testSkipQuestion(@PathVariable Long quizStateId) {
        logger.info("Admin testing skip question for quiz state ID: {}", quizStateId);
        QuizState quizState = getQuizState(quizStateId);
        userQuizStateService.processSkipQuestionSubmission(quizState);
        return ResponseEntity.ok(userQuizStateService.convertToDto(quizState));
    }

    /**
     * Tests the effect of a joker on the given quiz state.
     *
     * @param quizStateId the ID of the quiz state
     * @param jokerDto    the joker data transfer object
     * @return the updated quiz state DTO after applying the joker effect
     */
    @Operation(summary = "Test joker effect", description = "Tests the effect of a joker on the specified quiz state id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Joker effect applied successfully"),
            @ApiResponse(responseCode = "404", description = "QuizState not found")
    })
    @PostMapping("/{quizStateId}/joker")
    public ResponseEntity<QuizStateDto> testJokerEffect(
            @PathVariable Long quizStateId,
            @RequestBody JokerDto jokerDto) {
        logger.info("Admin testing joker of type {} for quiz state ID: {}",
                jokerDto.getIdString(), quizStateId);
        QuizState quizState = getQuizState(quizStateId);
        // TODO: Implement joker application logic
        return ResponseEntity.ok(userQuizStateService.convertToDto(quizState));
    }

    /**
     * Resets the given quiz state by ending the current state and starting a new one.
     *
     * @param quizStateId the ID of the quiz state to reset
     * @return the DTO of the new quiz state
     */
    @Operation(summary = "Reset quiz state", description = "Resets the specified quiz state by ending the current state and starting a new quiz")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quiz state reset successfully"),
            @ApiResponse(responseCode = "404", description = "QuizState not found")
    })
    @PostMapping("/{quizStateId}/reset")
    public ResponseEntity<QuizStateDto> resetQuizState(@PathVariable Long quizStateId) {
        logger.info("Admin resetting quiz state with ID: {}", quizStateId);
        QuizState quizState = getQuizState(quizStateId);
        // End the current quiz for the user
        userQuizStateService.processQuizEnd(quizState);
        // Start a new quiz for the same user
        QuizState newQuizState = userQuizStateService.startNewQuiz(quizState.getUserId());
        return ResponseEntity.ok(userQuizStateService.convertToDto(newQuizState));
    }

    /**
     * Forces ending of the quiz for the given quiz state.
     *
     * @param quizStateId the ID of the quiz state to end
     * @return the updated quiz state DTO after forcing the quiz end
     */
    @Operation(summary = "Force quiz end", description = "Forces end of quiz for the specified quiz state id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quiz ended successfully"),
            @ApiResponse(responseCode = "404", description = "QuizState not found")
    })
    @PostMapping("/{quizStateId}/end")
    public ResponseEntity<QuizStateDto> forceQuizEnd(@PathVariable Long quizStateId) {
        logger.info("Admin forcing quiz end for quiz state ID: {}", quizStateId);
        QuizState quizState = getQuizState(quizStateId);
        userQuizStateService.processQuizEnd(quizState);
        return ResponseEntity.ok(userQuizStateService.convertToDto(quizState));
    }

    /**
     * Helper method to retrieve a QuizState by ID.
     *
     * @param quizStateId the ID of the quiz state
     * @return the QuizState instance
     * @throws EntityNotFoundException if the quiz state is not found
     */
    private QuizState getQuizState(Long quizStateId) {
        return quizStateRepository.findById(quizStateId)
                .orElseThrow(() -> new EntityNotFoundException("QuizState not found with ID: " + quizStateId));
    }
}