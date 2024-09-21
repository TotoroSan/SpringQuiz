package com.example.quiz.controller.user;


import com.example.quiz.model.Answer;
import com.example.quiz.model.AnswerDto;
import com.example.quiz.model.Question;
import com.example.quiz.model.QuizState;
import com.example.quiz.service.admin.AdminAnswerService;
import com.example.quiz.service.user.UserAnswerService;

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

    // TODO how does a user interact with answers?
    
    // check is answer is true 
    @PostMapping("/answer")
    public ResponseEntity<Boolean> submitAnswer(@RequestBody AnswerDto answerDto, HttpSession session) {
        QuizState quizState = (QuizState) session.getAttribute("quizState");

        if (quizState == null) {
            return ResponseEntity.badRequest().body(null);
        }
        
        // get current question id from quizState
        //(alternative) Question question = questionServiceUser.findById(answerDto.getQuestionId()); (requires incorporation of questionId into answerDto)
        
        // let answer service validate the answer
        Question currentQuestion = quizState.getCurrentQuestion();
        
        boolean isCorrect = userAnswerService.isCorrectAnswer(answerDto, currentQuestion);

        if (!isCorrect) {
        	// TODO: decrease lifes / quiz failed -> restart / etc.
            return ResponseEntity.ok(false);
        }
        
        //DEBUGGING mark question as done
        quizState.markQuestionAsCompleted(currentQuestion.getId());
        
        //increment score 
        quizState.incrementScore();
        
        return ResponseEntity.ok(true);
    }
}
