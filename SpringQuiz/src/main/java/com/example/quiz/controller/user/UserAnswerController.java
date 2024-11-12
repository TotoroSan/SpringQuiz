package com.example.quiz.controller.user;


import com.example.quiz.model.dto.AnswerDto;
import com.example.quiz.model.entity.Answer;
import com.example.quiz.model.entity.Question;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.model.entity.User;
import com.example.quiz.service.admin.AdminAnswerService;
import com.example.quiz.service.user.UserAnswerService;
import com.example.quiz.service.user.UserQuizModifierService;
import com.example.quiz.service.user.UserQuizStateService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("user/api/answers")
public class UserAnswerController {

    @Autowired
    private UserAnswerService userAnswerService;

    @Autowired
    private UserQuizStateService userQuizStateService;

    @Autowired
    private UserQuizModifierService userQuizModifierService;

    @PostMapping("/answer")
    public ResponseEntity<Boolean> submitAnswer(@RequestBody AnswerDto answerDto,  @AuthenticationPrincipal User user, 
    		HttpSession session) {
    	
        // Retrieve the current quiz state from the database
        Long userId = user.getId();
        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestQuizStateByUserId(userId);
        
        if (optionalQuizState.isEmpty()) {
            System.out.println("Quiz State is null");
            return ResponseEntity.badRequest().body(null);
        }
        
        QuizState quizState = optionalQuizState.get(); 
       
        // Get the current question from state object
        Question currentQuestion = userQuizStateService.getCurrentQuestion(quizState);
        System.out.println("Loaded Quiz State: " + quizState);
        System.out.println("Current Question Index: " + quizState.getCurrentQuestionIndex());
        System.out.println("Current Question text: " + quizState.getAllQuestions().get(quizState.getCurrentQuestionIndex()));
        System.out.println("All Question IDs: " + quizState.getAllQuestions());
        System.out.println("Completed Question IDs: " + quizState.getCompletedQuestionIds());
        
        
        // Validate the answer correctness using the answer service
        boolean isCorrect = userAnswerService.isCorrectAnswer(answerDto, currentQuestion);

        if (!isCorrect) {
        	System.out.println("Answer is wrong"); // debug 
            // Handle incorrect answer (e.g., decrease lives)	
            return ResponseEntity.ok(false);
        }
        
        // If answer is correct, update the quiz state using the quiz state service
        // Todo consolidate this
        userQuizStateService.markQuestionAsCompleted(quizState, currentQuestion.getId());
        userQuizStateService.incrementScore(quizState);
        userQuizStateService.incrementCurrentRound(quizState);
        userQuizStateService.IncrementAnsweredQuestionsInSegment(quizState);

        // TODO move (maybe to UserStateService -> each time we fetch the state we clean the modifiers?)
        // reduce ModifierEffect duration and remove invalid ModifierEffects
        userQuizModifierService.processModifierEffectsForNewRound(quizState.getQuizModifier());
        
    	System.out.println("Answer is right"); // debug 
            
        // Persist the updated quiz state
        userQuizStateService.saveQuizState(quizState);
    	
        // Update the session with the updated QuizState
        session.setAttribute("quizState", quizState);

        return ResponseEntity.ok(true);
    }
}

