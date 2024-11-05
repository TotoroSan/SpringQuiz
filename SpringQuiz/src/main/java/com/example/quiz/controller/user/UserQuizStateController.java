package com.example.quiz.controller.user;


import com.example.quiz.model.dto.AnswerDto;
import com.example.quiz.model.dto.QuizStateDto;
import com.example.quiz.model.entity.Question;
import com.example.quiz.model.entity.Quiz;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.model.entity.User;
import com.example.quiz.service.admin.AdminQuizService;
import com.example.quiz.service.user.UserQuestionService;
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

    // TODO how does a user interact with quizzes?
    
    // start quiz (start session)
        
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

        // Convert to DTO to return to the user
        QuizStateDto quizStateDto = new QuizStateDto(
            quizState.getScore(),
            quizState.getCurrentRound(),
            quizState.getAllQuestions().isEmpty() ? null : quizState.getAllQuestions().get(quizState.getCurrentQuestionIndex()).getQuestionText()
        );

        return ResponseEntity.ok(quizStateDto);
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
