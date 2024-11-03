package com.example.quiz.controller.user;


import com.example.quiz.model.Answer;
import com.example.quiz.model.AnswerDto;
import com.example.quiz.model.Question;
import com.example.quiz.model.QuizState;
import com.example.quiz.service.admin.AdminAnswerService;
import com.example.quiz.service.user.UserAnswerService;
import com.example.quiz.service.user.UserQuizStateService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("user/api/answers")
public class UserAnswerController {

    @Autowired
    private UserAnswerService userAnswerService;

    @Autowired
    private UserQuizStateService userQuizStateService;

    @PostMapping("/answer")
    public ResponseEntity<Boolean> submitAnswer(@RequestBody AnswerDto answerDto, HttpSession session) {
        // Retrieve the current quiz state from the session
        QuizState quizState = (QuizState) session.getAttribute("quizState");

        if (quizState == null) {
            System.out.println("Quiz State is null");
            return ResponseEntity.badRequest().body(null);
        }
        
        // TODO BUGFIX currentQuestion is null 
        // Get the current question from state object
        Question currentQuestion = userQuizStateService.getCurrentQuestion(quizState);
        
        // Validate the answer correctness using the answer service
        boolean isCorrect = userAnswerService.isCorrectAnswer(answerDto, currentQuestion);

        if (!isCorrect) {
        	System.out.println("Answer is wrong"); // debug 
            // Handle incorrect answer (e.g., decrease lives)	
            return ResponseEntity.ok(false);
        }
        
        // If answer is correct, update the quiz state using the quiz state service
        userQuizStateService.markQuestionAsCompleted(quizState, currentQuestion.getId());
        userQuizStateService.incrementScore(quizState);
        userQuizStateService.incrementCurrentRound(quizState);
        
    	System.out.println("Answer is right"); // debug 
            
        // Update the session with the modified quiz state
        session.setAttribute("quizState", quizState);

        return ResponseEntity.ok(true);
    }
}

