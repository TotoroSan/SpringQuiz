package com.example.quiz.controller.admin;

import com.example.quiz.model.dto.QuizStateDto;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.model.entity.User;
import com.example.quiz.repository.QuizStateRepository;
import com.example.quiz.service.user.UserQuizStateService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    public AdminQuizStateController(
            QuizStateRepository quizStateRepository,
            UserQuizStateService userQuizStateService) {
        this.quizStateRepository = quizStateRepository;
        this.userQuizStateService = userQuizStateService;
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


}