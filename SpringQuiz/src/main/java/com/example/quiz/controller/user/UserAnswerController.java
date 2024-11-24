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

