package com.example.quiz.controller.user;


import com.example.quiz.model.dto.AnswerDto;
import com.example.quiz.model.entity.Question;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.model.entity.User;
import com.example.quiz.service.user.UserAnswerService;
import com.example.quiz.service.user.UserQuizModifierService;
import com.example.quiz.service.user.UserQuizStateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("user/api/answers")
public class UserAnswerController {
    private static final Logger logger = LoggerFactory.getLogger(UserAnswerController.class);

    @Autowired
    private UserAnswerService userAnswerService;

    @Autowired
    private UserQuizStateService userQuizStateService;

    @Autowired
    private UserQuizModifierService userQuizModifierService;

    /**
     * Submits an answer to the current question in the quiz.
     * Evaluates whether the answer is correct, updates the quiz state accordingly,
     * and handles correct or incorrect responses.
     *
     * @param answerDto The submitted answer data.
     * @param user The authenticated user submitting the answer.
     * @param session The HTTP session for tracking quiz state.
     * @return ResponseEntity with a Boolean value indicating whether the answer was correct.
     */
    @Operation(
            summary = "Submit an answer",
            description = """
        Submits an answer to the current question. The system verifies correctness,
        updates the quiz state, and processes correct or incorrect answers accordingly.
        """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Answer submitted successfully. Response contains true if correct, false if incorrect.",
            content = @Content(schema = @Schema(type = "boolean"))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Quiz state not found or invalid request",
            content = @Content
    )
    @PostMapping("/answer")
    public ResponseEntity<Boolean> submitAnswer(@RequestBody AnswerDto answerDto, @AuthenticationPrincipal User user,
                                                HttpSession session) {

        logger.info("Received request to evaluate submitted answer", answerDto.getText());

        // Retrieve the current quiz state from the database
        Long userId = user.getId();
        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestQuizStateByUserId(userId);

        if (optionalQuizState.isEmpty()) {
            logger.warn("Quiz state not found for user ID: {}", userId);
            return ResponseEntity.badRequest().body(null);
        }

        QuizState quizState = optionalQuizState.get();

        // Get the current question from state object
        Question currentQuestion = userQuizStateService.getCurrentQuestion(quizState);

        // Validate the answer correctness using the answer service
        boolean isCorrect = userAnswerService.isCorrectAnswer(answerDto, currentQuestion);

        if (!isCorrect) {
            // Handle incorrect answer response (e.g., decrease lives)
            logger.info("Submitted answer is incorrect");
            userQuizStateService.processIncorrectAnswerSubmission(quizState);
            return ResponseEntity.ok(false);
        }

        // If answer is correct, update the quiz state using the quiz state service
        logger.info("Submitted answer is correct");
        userQuizStateService.processCorrectAnswerSubmission(quizState);


        logger.debug("Successfully processed submitAnswer request");
        return ResponseEntity.ok(true);
    }
}

