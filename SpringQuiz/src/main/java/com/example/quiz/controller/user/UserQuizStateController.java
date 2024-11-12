package com.example.quiz.controller.user;


import com.example.quiz.model.dto.*;
import com.example.quiz.model.entity.*;
import com.example.quiz.service.user.UserQuestionService;
import com.example.quiz.service.user.UserQuizModifierService;
import com.example.quiz.service.user.UserQuizStateService;

import jakarta.servlet.http.HttpSession;

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
        Long userId = user.getId();
        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestQuizStateByUserId(userId);

        if (optionalQuizState.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        QuizState quizState = optionalQuizState.get();
        session.setAttribute("quizState", quizState);

        QuizStateDto quizStateDto = userQuizStateService.createQuizStateDto(quizState);

        return ResponseEntity.ok(quizStateDto);
    }

    // Endpoint to handle next game event (either a question or modifier effects)
    // TODO maybe create new wrapper class for the game events so that we do not have to return <?>
    @GetMapping("/nextGameEvent")
    public ResponseEntity<GameEventDto> getNextGameEvent(HttpSession session, @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestQuizStateByUserId(userId);

        if (optionalQuizState.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        QuizState quizState = optionalQuizState.get();
        session.setAttribute("quizState", quizState);

        // Check if the current round is divisible by 5 to provide modifier effects
        // TODO 5 is arbitrary value for testing.
        if (quizState.getAnsweredQuestionsInSegment() % 5 == 0) {
            List<QuizModifierEffectDto> randomQuizModifierEffects = userQuizModifierService.pickRandomModifierDtos();
            GameEventDto modifierEvent = new GameEventDto(randomQuizModifierEffects);
            return ResponseEntity.ok(modifierEvent);
        } else {
            // Return the next question with the given difficulty
            int currentDiffculty = 1 * quizState.getQuizModifier().getDifficultyModifier();

            // Debug
            System.out.println("Retreiving Question with difficulty of: " + currentDiffculty);

            // TODO adjust if any difficulty is wanted. just remove currentDifficulty as paramter
            Question currentQuestion = userQuestionService.getRandomQuestionExcludingCompleted(quizState.getCompletedQuestionIds(), currentDiffculty);
            userQuizStateService.addQuestion(quizState, currentQuestion);
;
            QuestionWithShuffledAnswersDto questionWithShuffledAnswersDto = userQuestionService.createQuestionWithShuffledAnswersDto(currentQuestion);
            GameEventDto questionEvent = new GameEventDto(questionWithShuffledAnswersDto);

            return ResponseEntity.ok(questionEvent);
        }
    }


    
    
    // Endpoint to get random QuizModifierEffects to present to the user
    @GetMapping("/modifiers")
    public ResponseEntity<List<QuizModifierEffectDto>> getRandomModifiers() {
        List<QuizModifierEffectDto> randomQuizModifierEffects = userQuizModifierService.pickRandomModifierDtos();
        return ResponseEntity.ok(randomQuizModifierEffects);
    }

    // Endpoint to apply the chosen modifier effect to the modifier of the gamestate
    @PostMapping("/modifiers/apply")
    public ResponseEntity<String> applyModifier(@RequestBody QuizModifierEffectDto quizModifierEffectDto, @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestQuizStateByUserId(userId);

        if (optionalQuizState.isEmpty()) {
            return ResponseEntity.badRequest().body("Quiz state not found");
        }

        QuizState quizState = optionalQuizState.get();
        QuizModifier quizModifier = quizState.getQuizModifier();
        boolean success = userQuizModifierService.applyModifierById(quizModifier, quizModifierEffectDto.getId());


        if (success) {
            userQuizStateService.moveToNextSegment(quizState);
            return ResponseEntity.ok("Modifier applied successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to apply modifier");
        }
    }
   
}
