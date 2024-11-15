package com.example.quiz.controller.user;


import com.example.quiz.model.dto.AnswerDto;
import com.example.quiz.model.entity.Question;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.model.entity.User;
import com.example.quiz.service.user.UserAnswerService;
import com.example.quiz.service.user.UserQuizModifierService;
import com.example.quiz.service.user.UserQuizStateService;

import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/answer")
    public ResponseEntity<Boolean> submitAnswer(@RequestBody AnswerDto answerDto,  @AuthenticationPrincipal User user, 
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
            logger.debug("Submitted answer is incorrect");
            // Handle incorrect answer (e.g., decrease lives)	
            return ResponseEntity.ok(false);
        }
        
        // If answer is correct, update the quiz state using the quiz state service
        // Todo consolidate this
        userQuizStateService.markQuestionAsCompleted(quizState, currentQuestion.getId());
        userQuizStateService.incrementScore(quizState);
        userQuizStateService.incrementCurrentRound(quizState);
        userQuizStateService.IncrementAnsweredQuestionsInSegment(quizState);

        userQuizModifierService.processActiveQuizModifierEffectsForNewRound(quizState.getQuizModifier());

        // Persist the updated quiz state
        userQuizStateService.saveQuizState(quizState);
    	
        // Update the session with the updated QuizState
        session.setAttribute("quizState", quizState);

        logger.debug("Submitted answer is correct");
        return ResponseEntity.ok(true);
    }
}

