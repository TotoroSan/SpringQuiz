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
    public AdminGameEventController(
            QuizStateRepository quizStateRepository,
            UserQuizStateService userQuizStateService,
            UserGameEventService userGameEventService,
            UserJokerService userJokerService) {
        this.quizStateRepository = quizStateRepository;
        this.userQuizStateService = userQuizStateService;
        this.userGameEventService = userGameEventService;
        this.userJokerService = userJokerService;
    }

    /**
     * Forces a question event for a quiz state.
     *
     * @param quizStateId The ID of the quiz state
     * @return The created game event DTO
     */
    @PostMapping("/{quizStateId}/question")
    public ResponseEntity<GameEventDto> forceQuestionEvent(@PathVariable Long quizStateId) {
        logger.info("Admin forcing question event for quiz state ID: {}", quizStateId);

        QuizState quizState = getQuizState(quizStateId);
        GameEvent gameEvent = userQuizStateService.createQuestionGameEvent(quizState);

        return ResponseEntity.ok(userGameEventService.convertToDto(gameEvent));
    }

    /**
     * Forces a shop event for a quiz state.
     *
     * @param quizStateId The ID of the quiz state
     * @return The created game event DTO
     */
    @PostMapping("/{quizStateId}/shop")
    public ResponseEntity<GameEventDto> forceShopEvent(@PathVariable Long quizStateId) {
        logger.info("Admin forcing shop event for quiz state ID: {}", quizStateId);

        QuizState quizState = getQuizState(quizStateId);
        GameEvent gameEvent = userQuizStateService.createShopGameEvent(quizState);

        return ResponseEntity.ok(userGameEventService.convertToDto(gameEvent));
    }

    /**
     * Forces a modifier effects event for a quiz state.
     *
     * @param quizStateId The ID of the quiz state
     * @return The created game event DTO
     */
    @PostMapping("/{quizStateId}/modifier")
    public ResponseEntity<GameEventDto> forceModifierEvent(@PathVariable Long quizStateId) {
        logger.info("Admin forcing modifier effects event for quiz state ID: {}", quizStateId);

        QuizState quizState = getQuizState(quizStateId);
        GameEvent gameEvent = userQuizStateService.createModifierEffectsGameEvent(quizState);

        return ResponseEntity.ok(userGameEventService.convertToDto(gameEvent));
    }



    /**
     * Tests skipping a question for a quiz state.
     *
     * @param quizStateId The ID of the quiz state
     * @return The updated quiz state DTO
     */
    @PostMapping("/{quizStateId}/skip")
    public ResponseEntity<QuizStateDto> testSkipQuestion(@PathVariable Long quizStateId) {
        logger.info("Admin testing skip question for quiz state ID: {}", quizStateId);

        QuizState quizState = getQuizState(quizStateId);
        userQuizStateService.processSkipQuestionSubmission(quizState);

        return ResponseEntity.ok(userQuizStateService.convertToDto(quizState));
    }

    /**
     * Tests the effect of a joker on a quiz state.
     *
     * @param quizStateId The ID of the quiz state
     * @param jokerDto The joker data
     * @return The updated quiz state DTO
     */
    @PostMapping("/{quizStateId}/joker")
    public ResponseEntity<QuizStateDto> testJokerEffect(
            @PathVariable Long quizStateId,
            @RequestBody JokerDto jokerDto) {
        logger.info("Admin testing joker of type {} for quiz state ID: {}",
                jokerDto.getIdString(), quizStateId);

        QuizState quizState = getQuizState(quizStateId);

        // Create a joker instance for testing
        // TODO

        // Apply the joker
        // TODO
        return ResponseEntity.ok(userQuizStateService.convertToDto(quizState));
    }

    /**
     * Resets a quiz state - ends the current quiz and starts a new one.
     *
     * @param quizStateId The ID of the quiz state to reset
     * @return The new quiz state DTO
     */
    @PostMapping("/{quizStateId}/reset")
    public ResponseEntity<QuizStateDto> resetQuizState(@PathVariable Long quizStateId) {
        logger.info("Admin resetting quiz state with ID: {}", quizStateId);

        QuizState quizState = getQuizState(quizStateId);

        // End the current quiz
        userQuizStateService.processQuizEnd(quizState);

        // Start a new quiz for the same user
        QuizState newQuizState = userQuizStateService.startNewQuiz(quizState.getUserId());

        return ResponseEntity.ok(userQuizStateService.convertToDto(newQuizState));
    }

    /**
     * Forces ending a quiz.
     *
     * @param quizStateId The ID of the quiz state to end
     * @return The updated quiz state DTO
     */
    @PostMapping("/{quizStateId}/end")
    public ResponseEntity<QuizStateDto> forceQuizEnd(@PathVariable Long quizStateId) {
        logger.info("Admin forcing quiz end for quiz state ID: {}", quizStateId);

        QuizState quizState = getQuizState(quizStateId);
        userQuizStateService.processQuizEnd(quizState);

        return ResponseEntity.ok(userQuizStateService.convertToDto(quizState));
    }


    /**
     * Helper method to retrieve a quiz state by ID.
     *
     * @param quizStateId The ID of the quiz state to retrieve
     * @return The quiz state
     * @throws EntityNotFoundException if the quiz state is not found
     */
    private QuizState getQuizState(Long quizStateId) {
        return quizStateRepository.findById(quizStateId)
                .orElseThrow(() -> new EntityNotFoundException("QuizState not found with ID: " + quizStateId));
    }


}