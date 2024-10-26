package com.example.quiz.controller.user;

import com.example.quiz.model.AnswerDto;
import com.example.quiz.model.Question;
import com.example.quiz.model.QuizState;
import com.example.quiz.service.user.UserAnswerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user/api/answers")
public class UserAnswerController {

    @Autowired
    private UserAnswerService userAnswerService;

    // User submits an answer
    @PostMapping("/answer")
    public ResponseEntity<Boolean> submitAnswer(@RequestBody AnswerDto answerDto, HttpSession session) {
        QuizState quizState = (QuizState) session.getAttribute("quizState");

        if (quizState == null) {
            return ResponseEntity.badRequest().body(null);
        }

        // Get the current question from the quiz state
        Question currentQuestion = quizState.getCurrentQuestion();
        if (currentQuestion == null) {
            return ResponseEntity.badRequest().body(null);
        }

        // Let answer service validate the answer
        boolean isCorrect = userAnswerService.isCorrectAnswer(answerDto, currentQuestion);

        if (!isCorrect) {
<<<<<<< HEAD
            // TODO: decrease lives / handle quiz failure
=======
        	// TODO: decrease lifes / quiz failed -> restart / etc.
>>>>>>> parent of 1a2ebc3 (Update)
            return ResponseEntity.ok(false);
        }

        // Mark question as completed and increment score
        quizState.markQuestionAsCompleted(currentQuestion.getId());
        quizState.incrementScore();

        return ResponseEntity.ok(true);
    }
}
