package com.example.quiz.controller.user;

import com.example.quiz.model.Question;
import com.example.quiz.model.QuestionDto;
import com.example.quiz.model.QuestionWithShuffledAnswersDto;
import com.example.quiz.model.QuizState;
import com.example.quiz.service.admin.AdminQuestionService;
import com.example.quiz.service.user.UserQuestionService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("user/api/questions")
public class UserQuestionController {

    @Autowired
    private UserQuestionService userQuestionService;

    // TODO how does a user interact with questions?

    // Get a random question with shuffled answers including realAnswer and answers from the mock-answer pool
    @GetMapping
    public ResponseEntity<QuestionWithShuffledAnswersDto> getRandomQuestionWithShuffledAnswers(HttpSession session) {
    	QuizState quizState = (QuizState) session.getAttribute("quizState");
    	
    	// add next question to session
    	Question currentQuestion = userQuestionService.getRandomQuestion();
    	quizState.addQuestion(currentQuestion);
    	quizState.incrementQuestionIndex();
    	
    	System.out.println("Mock answers before shuffle after load" + currentQuestion.getMockAnswers());
    	
        QuestionWithShuffledAnswersDto questionWithShuffledAnswersDto = userQuestionService.createQuestionWithShuffledAnswersDto(currentQuestion);
        
         
        return ResponseEntity.ok(questionWithShuffledAnswersDto); 
        // set new question as active 
    }
    
    // get next question (if we run a prepared quiz)
    
    // get the next question with specific rank
    
    // check validity of answer (might be moved to answer service later)
    
    // update session 
    
}
