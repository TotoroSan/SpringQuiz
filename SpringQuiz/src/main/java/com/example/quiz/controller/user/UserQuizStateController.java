package com.example.quiz.controller.user;


import com.example.quiz.model.dto.*;
import com.example.quiz.model.entity.*;
import com.example.quiz.service.user.UserQuestionService;
import com.example.quiz.service.user.UserQuizModifierService;
import com.example.quiz.service.user.UserQuizStateService;

import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// having the same URI for different actions (like create, update, and delete) but distinguishing them by the HTTP method (POST, PUT, DELETE, etc.) is indeed the best practice in RESTful API design.

@RestController
@RequestMapping("user/api/quiz")
public class UserQuizStateController {
    private static final Logger logger = LoggerFactory.getLogger(UserQuizStateController.class);

    // this is the controller for quiz management and users accessing session data
	
	// we will route requests to different controllers to keep separation of concern.
	// we will update the session data from different controllers, so we can update in one go.
	// for later: (its possible to first confirm question correctness and then update with second requesst to have centralized place for session)
	
    @Autowired
    private UserQuizStateService userQuizStateService;
    
    @Autowired
    private UserQuizModifierService userQuizModifierService;

    @Autowired
    private UserQuestionService userQuestionService;

    // TODO how does a user interact with quizzes?

    // Start a new quiz
    @GetMapping("/start")
    public ResponseEntity<String> startQuiz(HttpSession session, @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        QuizState quizState = userQuizStateService.startNewQuiz(userId);
        session.setAttribute("quizState", quizState); // we use session as a "hot storage" for QuizState
        return ResponseEntity.ok("Quiz started!");
    }
    
    // Get QuizState
    /* 
     * @AuthenticationPrincipal annotation is used to directly inject the currently authenticated user into a method parameter.
     * Specifically, it extracts the user details from the authentication token, which means the authenticated user's information
     * is available for use without the need to manually parse the JWT or session. 
     * */
    @GetMapping("/state")
    public ResponseEntity<QuizStateDto> getQuizState(HttpSession session, @AuthenticationPrincipal User user) {
        logger.info("Received request to get QuizState for user: {}", user.getId());

        Long userId = user.getId();
        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestQuizStateByUserId(userId);

        if (optionalQuizState.isEmpty()) {
            logger.warn("Quiz state not found for user ID: {}", userId);
            return ResponseEntity.badRequest().build();
        }

        QuizState quizState = optionalQuizState.get();
        session.setAttribute("quizState", quizState);


        QuizStateDto quizStateDto = userQuizStateService.convertToDto(quizState);

        logger.debug("Successfully retreived QuizState");
        return ResponseEntity.ok(quizStateDto);
    }

    // Endpoint to handle next game event (either a question or modifier effects)
    @GetMapping("/nextGameEvent")
    public ResponseEntity<GameEventDto> getNextGameEvent(HttpSession session, @AuthenticationPrincipal User user) {
        logger.info("Received request to get next game event for user ID: {}", user.getId());


        Long userId = user.getId();
        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestQuizStateByUserId(userId);

        // todo should this raise an exception?
        if (optionalQuizState.isEmpty()) {
            logger.warn("Quiz state not found for user ID: {}", userId);
            return ResponseEntity.badRequest().build();
        }

        QuizState quizState = optionalQuizState.get();
        session.setAttribute("quizState", quizState);

        // Check if the current round is divisible by 5 to provide modifier effects
        // TODO 5 is arbitrary value for testing.
        if (quizState.getAnsweredQuestionsInSegment() % 5 == 0) {
            logger.info("Returning random modifier effects for user ID: {}", userId);
            List<QuizModifierEffectDto> randomQuizModifierEffects = userQuizModifierService.pickRandomModifierEffectDtos();
            GameEventDto modifierEvent = new GameEventDto(randomQuizModifierEffects);
            logger.debug("Successfully returned random modifier effects");
            return ResponseEntity.ok(modifierEvent);
        } else {
            logger.info("Returning next question for user ID: {}", userId);

            // Return the next question with the given difficulty
            int currentDiffculty = 1 * quizState.getQuizModifier().getDifficultyModifier();
            // TODO adjust if any difficulty is wanted. just remove currentDifficulty as paramter
            Question currentQuestion = userQuestionService.getRandomQuestionExcludingCompleted(quizState.getCompletedQuestionIds(), currentDiffculty);
            userQuizStateService.addQuestion(quizState, currentQuestion);
            QuestionWithShuffledAnswersDto questionWithShuffledAnswersDto = userQuestionService.createQuestionWithShuffledAnswersDto(currentQuestion);
            GameEventDto questionEvent = new GameEventDto(questionWithShuffledAnswersDto);

            logger.debug("Successfully returned QuestionWithShuffledAnswers");
            return ResponseEntity.ok(questionEvent);
        }
    }


    
    
    // Endpoint to get random QuizModifierEffects to present to the user
    // TODO currently not in use
    @GetMapping("/modifiers/getrandom")
    public ResponseEntity<List<QuizModifierEffectDto>> getRandomModifiers() {
        List<QuizModifierEffectDto> randomQuizModifierEffects = userQuizModifierService.pickRandomModifierEffectDtos();
        return ResponseEntity.ok(randomQuizModifierEffects);
    }

    // Endpoint to apply the chosen modifier effect to the modifier of the gamestate
    @PostMapping("/modifiers/apply")
    public ResponseEntity<String> applyModifier(HttpSession session, @RequestBody QuizModifierEffectDto quizModifierEffectDto, @AuthenticationPrincipal User user) {
        logger.info("Received request to apply chosen QuizModifierEffect for user ID: {}", user.getId());

        Long userId = user.getId();
        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestQuizStateByUserId(userId);

        if (optionalQuizState.isEmpty()) {
            logger.warn("Quiz state not found for user ID: {}", userId);
            return ResponseEntity.badRequest().body("Quiz state not found");
        }

        QuizState quizState = optionalQuizState.get();
        QuizModifier quizModifier = quizState.getQuizModifier();
        boolean success = userQuizModifierService.applyModifierEffectById(quizModifier, quizModifierEffectDto.getId());


        if (success) {
            userQuizStateService.moveToNextSegment(quizState);
            session.setAttribute("quizState", quizState);
            userQuizStateService.saveQuizState(quizState); // TODO move saving logic to service (?)
            logger.info("Successfully applied chosen QuizModifierEffect");
            return ResponseEntity.ok("Modifier applied successfully");
        } else {
            logger.error("Failed to apply chosen QuizModifierEffect");
            return ResponseEntity.badRequest().body("Failed to apply modifier");
        }
    }


    @GetMapping("/modifiers/getactive")
    public ResponseEntity<List<QuizModifierEffectDto>> getActiveQuizModifierDtos(@AuthenticationPrincipal User user) {
        logger.info("Received request to get ActiveQuizModifierDtos for user: {}", user.getId());

        Long userId = user.getId();
        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestQuizStateByUserId(userId);

        if (optionalQuizState.isEmpty()) {
            logger.warn("Quiz state not found for user ID: {}", userId);
            return ResponseEntity.badRequest().build();
        }

        QuizState quizState = optionalQuizState.get();
        List<QuizModifierEffectDto> activeQuizModifierEffects = userQuizModifierService.getActiveModifierEffectDtos(quizState.getQuizModifier());

        logger.info("Successfully retreived ActiveQuizModifierDtos for user: {}", user.getId());

        return ResponseEntity.ok(activeQuizModifierEffects);
    }
}
