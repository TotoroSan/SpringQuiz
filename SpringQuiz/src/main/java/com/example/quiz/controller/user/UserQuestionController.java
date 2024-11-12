package com.example.quiz.controller.user;

import com.example.quiz.model.dto.QuestionDto;
import com.example.quiz.model.dto.QuestionWithShuffledAnswersDto;
import com.example.quiz.model.entity.Question;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.model.entity.User;
import com.example.quiz.service.admin.AdminQuestionService;
import com.example.quiz.service.user.UserQuestionService;
import com.example.quiz.service.user.UserQuizStateService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("user/api/questions")
public class UserQuestionController {

    @Autowired
    private UserQuestionService userQuestionService;
    
    @Autowired
    private UserQuizStateService userQuizStateService;
    // TODO how does a user interact with questions?

    // Get a random question with shuffled answers including realAnswer and answers from the mock-answer pool
    // TODO currently not in use. QuizStateController handles quiz logic.
    @GetMapping
    public ResponseEntity<QuestionWithShuffledAnswersDto> getRandomQuestionWithShuffledAnswers(HttpSession session, @AuthenticationPrincipal User user) {
    	
        // Fetch the current user ID
        Long userId = user.getId();

        // Retrieve the QuizState from the database using the userId
        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestQuizStateByUserId(userId);

        if (optionalQuizState.isEmpty()) {
            System.out.println("Quiz State is null");
            return ResponseEntity.badRequest().build();
        }

        // Get the QuizState from Optional
        QuizState quizState = optionalQuizState.get();
    	
    	// Add next question to QuizState
    	Question currentQuestion = userQuestionService.getRandomQuestionExcludingCompleted(quizState.getCompletedQuestionIds());
    	userQuizStateService.addQuestion(quizState, currentQuestion);


        // Update the session with the modified QuizState for quick access
        session.setAttribute("quizState", quizState);
    	
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
