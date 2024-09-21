package com.example.quiz.controller.user;


import com.example.quiz.model.AnswerDto;
import com.example.quiz.model.Question;
import com.example.quiz.model.Quiz;
import com.example.quiz.model.QuizState;
import com.example.quiz.service.admin.AdminQuizService;
import com.example.quiz.service.user.UserQuestionService;
import com.example.quiz.service.user.UserQuizService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// having the same URI for different actions (like create, update, and delete) but distinguishing them by the HTTP method (POST, PUT, DELETE, etc.) is indeed the best practice in RESTful API design.

@RestController
@RequestMapping("user/api/quizzes")
public class UserQuizController {
	// this is the controller for quiz management and users accessing session data
	
	// we will route requests to different controllers to keep separation of concern.
	// we will update the session data from different controllers, so we can update in one go.
	// for later: (its possible to first confirm question correctness and then update with second requesst to have centralized place for session)
	
    @Autowired
    private UserQuizService userQuizService;

    // TODO how does a user interact with quizzes?
    
    // start quiz (start session)
        
    @GetMapping("/start")
    public ResponseEntity<String> startQuiz(HttpSession session) {
        // Initialize a new quiz and store the state in the session
        QuizState quizState = userQuizService.startNewQuiz();
        session.setAttribute("quizState", quizState);
        return ResponseEntity.ok("Quiz started!");
    }

//    @GetMapping("/question")
//    public ResponseEntity<QuestionDto> getNextQuestion(HttpSession session) {
//        QuizState quizState = (QuizState) session.getAttribute("quizState");
//
//        if (quizState == null || !quizState.hasMoreQuestions()) {
//            return ResponseEntity.badRequest().body(null);  // No quiz or no more questions
//        }
//
//        // Get the next question
//        Question nextQuestion = quizService.getNextQuestion(quizState);
//        return ResponseEntity.ok(new QuestionDto(nextQuestion));
//    }


  
    // restart
    
    // show session data  
    
    // end / save quiz

}
