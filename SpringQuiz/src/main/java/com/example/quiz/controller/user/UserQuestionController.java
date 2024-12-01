package com.example.quiz.controller.user;

import com.example.quiz.model.dto.GameEventDto;
import com.example.quiz.model.entity.Question;
import com.example.quiz.model.entity.QuestionGameEvent;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.model.entity.User;
import com.example.quiz.service.user.UserGameEventService;
import com.example.quiz.service.user.UserQuestionService;
import com.example.quiz.service.user.UserQuizStateService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("user/api/questions")
public class UserQuestionController {
    private static final Logger logger = LoggerFactory.getLogger(UserQuestionController.class);

    @Autowired
    private UserQuestionService userQuestionService;

    @Autowired
    private UserGameEventService userGameEventService;

    @Autowired
    private UserQuizStateService userQuizStateService;
    // TODO how does a user interact with questions?

    // Get a random question with shuffled answers including realAnswer and answers from the mock-answer pool
    // TODO currently not in use. QuizStateController handles quiz logic.
    // depreceated
    @GetMapping
    public ResponseEntity<GameEventDto> getRandomQuestionWithShuffledAnswers(HttpSession session, @AuthenticationPrincipal User user) {
        logger.info("Received request to get QuestionWithShuffledAnswers for user ID: {}", user.getId());
        // Fetch the current user ID
        Long userId = user.getId();

        // Retrieve the QuizState from the database using the userId
        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestQuizStateByUserId(userId);

        if (optionalQuizState.isEmpty()) {
            logger.warn("Quiz state not found for user ID: {}", userId);
            return ResponseEntity.badRequest().build();
        }

        // Get the QuizState from Optional
        QuizState quizState = optionalQuizState.get();

        // Add next question to QuizState
        Question currentQuestion = userQuestionService.getRandomQuestionExcludingCompleted(quizState.getCompletedQuestionIds());
        userQuizStateService.addQuestion(quizState, currentQuestion);

        // Update the session with the modified QuizState for quick access
        session.setAttribute("quizState", quizState);

        QuestionGameEvent questionGameEvent = userQuestionService.createQuestionGameEvent(currentQuestion, quizState);
        GameEventDto questionGameEventDto = userGameEventService.convertToDto(questionGameEvent);
        logger.info("Successfully retreived a QuestionWithShuffledAnswers");
        return ResponseEntity.ok(questionGameEventDto);
        // set new question as active 
    }

    // get next question (if we run a prepared quiz)

    // get the next question with specific rank

    // check validity of answer (might be moved to answer service later)

    // update session 

}
