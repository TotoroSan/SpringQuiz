package com.example.quiz.controller.admin;

import com.example.quiz.model.dto.JokerDto;
import com.example.quiz.model.dto.QuizStateDto;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.model.entity.User;
import com.example.quiz.repository.QuizStateRepository;
import com.example.quiz.service.user.UserQuizStateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.quiz.service.admin.AdminJokerService;

import java.util.Optional;


/**
 * Controller for administrative actions related to quiz states.
 * Provides endpoints for managing quiz states for testing purposes.
 */
@RestController
@RequestMapping("/admin/api/quiz")
@PreAuthorize("hasRole('ADMIN')")
public class AdminQuizStateController {
    private static final Logger logger = LoggerFactory.getLogger(AdminQuizStateController.class);

    private final QuizStateRepository quizStateRepository;
    private final UserQuizStateService userQuizStateService;
    private final AdminJokerService adminJokerService;

    @Autowired
    public AdminQuizStateController(
            QuizStateRepository quizStateRepository,
            UserQuizStateService userQuizStateService, AdminJokerService adminJokerService) {
        this.quizStateRepository = quizStateRepository;
        this.userQuizStateService = userQuizStateService;
        this.adminJokerService = adminJokerService;
    }

    /**
     * Starts a new quiz for the authenticated user.
     * This endpoint allows admins to initiate a new quiz for themselves.
     *
     * @param user The currently authenticated user
     * @return The newly created quiz state DTO
     */
    @PostMapping("/start")
    public ResponseEntity<QuizStateDto> startQuiz(@AuthenticationPrincipal User user) {
        Long userId = user.getId();
        logger.info("Admin starting new quiz for user ID: {}", userId);

        QuizState quizState = userQuizStateService.startNewQuiz(userId);
        return ResponseEntity.ok(userQuizStateService.convertToDto(quizState));
    }

    /**
     * Forces a quiz state to be marked as complete.
     *
     * @param user The currently authenticated user
     * @return The updated quiz state DTO
     */
    @PutMapping("/{quizStateId}/complete")
    public ResponseEntity<QuizStateDto> forceCompleteQuiz(@AuthenticationPrincipal User user) {
        logger.info("Admin forcing completion of quiz state for user ID: {}", user.getId());

        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestQuizStateByUserId(user.getId());
        if (optionalQuizState.isEmpty()) {
            logger.warn("Quiz state not found for user ID: {}", user.getId());
            return ResponseEntity.badRequest().build();
        }

        QuizState quizState = optionalQuizState.get();
        quizState.setActive(false);
        QuizState savedState = quizStateRepository.save(quizState);

        return ResponseEntity.ok(userQuizStateService.convertToDto(savedState));
    }

    /**
     * Updates the current question index of a quiz state.
     *
     * @param user The currently authenticated user
     * @param questionIndex The new question index
     * @return The updated quiz state DTO
     */
    @PutMapping("/{quizStateId}/question/{questionIndex}")
    public ResponseEntity<QuizStateDto> updateCurrentQuestion(
            @AuthenticationPrincipal User user,
            @PathVariable int questionIndex) {
        logger.info("Admin updating current question to index {} for user ID: {}", questionIndex, user.getId());

        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestQuizStateByUserId(user.getId());
        if (optionalQuizState.isEmpty()) {
            logger.warn("Quiz state not found for user ID: {}", user.getId());
            return ResponseEntity.badRequest().build();
        }

        QuizState quizState = optionalQuizState.get();
        quizState.setCurrentQuestionIndex(questionIndex);
        userQuizStateService.saveQuizState(quizState);

        return ResponseEntity.ok(userQuizStateService.convertToDto(quizState));
    }

    /**
     * Updates the score of a quiz state.
     *
     * @param user The currently authenticated user
     * @param score The new score
     * @return The updated quiz state DTO
     */
    @PutMapping("/{quizStateId}/score/{score}")
    public ResponseEntity<QuizStateDto> updateScore(
            @AuthenticationPrincipal User user,
            @PathVariable int score) {
        logger.info("Admin updating score to {} for user ID: {}", score, user.getId());

        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestQuizStateByUserId(user.getId());
        if (optionalQuizState.isEmpty()) {
            logger.warn("Quiz state not found for user ID: {}", user.getId());
            return ResponseEntity.badRequest().build();
        }

        QuizState quizState = optionalQuizState.get();
        quizState.setScore(score);
        userQuizStateService.saveQuizState(quizState);

        return ResponseEntity.ok(userQuizStateService.convertToDto(quizState));
    }

    /**
     * Retrieves the current QuizState for the authenticated user and converts it into a QuizStateDto.
     * If no QuizState is found, returns a 400 Bad Request.
     *
     * @param user The currently authenticated user
     * @return A ResponseEntity containing the QuizStateDto, or 400 if no QuizState is found
     */
    @GetMapping("/state")
    public ResponseEntity<QuizStateDto> getQuizState(@AuthenticationPrincipal User user) {
        logger.info("Received request to get QuizState for user: {}", user.getId());

        Long userId = user.getId();
        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestQuizStateByUserId(userId);

        if (optionalQuizState.isEmpty()) {
            logger.warn("Quiz state not found for user ID: {}", userId);
            return ResponseEntity.badRequest().build();
        }

        QuizState quizState = optionalQuizState.get();
        QuizStateDto quizStateDto = userQuizStateService.convertToDto(quizState);

        logger.debug("Successfully retrieved QuizState");
        return ResponseEntity.ok(quizStateDto);
    }

    /**
     * Resets the quiz state of the authenticated user by ending the current state and starting a new quiz.
     *
     * @param user The authenticated user
     * @return A ResponseEntity containing the new QuizStateDto
     * @throws EntityNotFoundException if the active QuizState is not found
     */
    @Operation(summary = "Reset quiz state", description = "Resets the active quiz state of the authenticated user by ending the current state and starting a new quiz")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quiz state reset successfully"),
            @ApiResponse(responseCode = "404", description = "Active QuizState not found")
    })
    @PostMapping("/reset")
    public ResponseEntity<QuizStateDto> resetQuizState(@AuthenticationPrincipal User user) {
        logger.info("Admin resetting quiz state for user ID: {}", user.getId());
        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestActiveQuizStateByUserId(user.getId());
        if (optionalQuizState.isEmpty()) {
            throw new EntityNotFoundException("Active QuizState not found for user ID: " + user.getId());
        }
        QuizState quizState = optionalQuizState.get();
        userQuizStateService.processQuizEnd(quizState);
        QuizState newQuizState = userQuizStateService.startNewQuiz(quizState.getUserId());
        return ResponseEntity.ok(userQuizStateService.convertToDto(newQuizState));
    }

    /**
     * Forces the end of the active quiz state of the authenticated user.
     *
     * @param user The authenticated user
     * @return A ResponseEntity containing the QuizStateDto of the ended quiz state
     * @throws EntityNotFoundException if the active QuizState is not found
     */
    @Operation(summary = "Force quiz end", description = "Forces the end of the active quiz state for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quiz ended successfully"),
            @ApiResponse(responseCode = "404", description = "Active QuizState not found")
    })
    @PostMapping("/end")
    public ResponseEntity<QuizStateDto> forceQuizEnd(@AuthenticationPrincipal User user) {
        logger.info("Admin forcing quiz end for user ID: {}", user.getId());
        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestActiveQuizStateByUserId(user.getId());
        if (optionalQuizState.isEmpty()) {
            throw new EntityNotFoundException("Active QuizState not found for user ID: " + user.getId());
        }
        QuizState quizState = optionalQuizState.get();
        userQuizStateService.processQuizEnd(quizState);
        return ResponseEntity.ok(userQuizStateService.convertToDto(quizState));
    }

    /**
     * Tests the joker effect on the active quiz state of the authenticated user.
     * This endpoint allows admins to test different joker effects without owning the joker.
     *
     * @param user The authenticated user
     * @param jokerIdString The identifier string of the joker type to test
     * @param tier The tier of the joker (determines power level)
     * @return A ResponseEntity containing the QuizStateDto after applying the joker effect
     * @throws EntityNotFoundException if the active QuizState is not found
     */
    @PostMapping("/joker/test")
    @Operation(summary = "Test joker effect", description = "Tests the effect of a joker on the active quiz state without requiring the joker to be owned")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Joker effect applied successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid joker type or could not apply joker effect"),
            @ApiResponse(responseCode = "404", description = "Active QuizState not found")
    })
    public ResponseEntity<QuizStateDto> testJokerEffect(
            @AuthenticationPrincipal User user,
            @RequestParam @Parameter(description = "The identifier string of the joker (e.g., 'FIFTY_FIFTY')") String jokerIdString,
            @RequestParam(defaultValue = "1") @Parameter(description = "The tier level of the joker (default: 1)") Integer tier) {
        logger.info("Admin testing joker of type {} with tier {} for user ID: {}", jokerIdString, tier, user.getId());

        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestActiveQuizStateByUserId(user.getId());
        if (optionalQuizState.isEmpty()) {
            throw new EntityNotFoundException("Active QuizState not found for user ID: " + user.getId());
        }

        QuizState quizState = optionalQuizState.get();
        boolean success = adminJokerService.applyJokerForTesting(quizState, jokerIdString, tier);

        if (!success) {
            logger.warn("Failed to apply joker effect of type: {} with tier: {}", jokerIdString, tier);
            return ResponseEntity.badRequest().body(null);
        }

        // Save the QuizState after applying the joker effect
        userQuizStateService.saveQuizState(quizState);
        return ResponseEntity.ok(userQuizStateService.convertToDto(quizState));
    }
}