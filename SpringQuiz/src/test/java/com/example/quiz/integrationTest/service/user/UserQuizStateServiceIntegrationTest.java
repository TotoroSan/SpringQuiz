// File: src/integrationTest/java/com/example/quiz/service/user/UserQuizStateServiceIntegrationTest.java
package com.example.quiz.integrationTest.service.user;

import com.example.quiz.model.entity.GameEvent;
import com.example.quiz.model.entity.Question;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.repository.QuizStateRepository;
import com.example.quiz.service.user.UserQuizStateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class UserQuizStateServiceIntegrationTest {

    @Autowired
    private UserQuizStateService quizStateService;

    @Autowired
    private QuizStateRepository quizStateRepository;

    @Test
    public void testStartNewQuiz() {
        Long userId = 1L;
        QuizState quizState = quizStateService.startNewQuiz(userId);
        assertNotNull(quizState);
        assertTrue(quizState.isActive());

        Optional<QuizState> activeState = quizStateService.getLatestActiveQuizStateByUserId(userId);
        assertTrue(activeState.isPresent());
        assertEquals(userId, activeState.get().getUserId());
    }

    @Test
    public void testGetNextGameEvent() {
        Long userId = 2L;
        QuizState quizState = quizStateService.startNewQuiz(userId);

        // Calling getNextGameEvent should add a game event (usually a QuestionGameEvent)
        GameEvent gameEvent = quizStateService.getNextGameEvent(quizState);
        assertNotNull(gameEvent);
        assertNotNull(gameEvent.getGameEventType());
    }

    @Test
    public void testProcessCorrectAnswerSubmission() {
        Long userId = 3L;
        QuizState quizState = quizStateService.startNewQuiz(userId);

        // Add a dummy question to the quiz state so that processCorrectAnswerSubmission can use it.
        Question dummyQuestion = new Question();
        dummyQuestion.setId(100L);
        dummyQuestion.setQuestionText("What is 2+2?");
        dummyQuestion.setDifficulty(1);
        quizState.getAllQuestions().add(dummyQuestion);
        quizState.setCurrentQuestionIndex(0);

        // Process a correct answer.
        quizStateService.processCorrectAnswerSubmission(quizState);

        // Verify that the quiz round is incremented and score is updated.
        assertTrue(quizState.getCurrentRound() >= 1, "Current round should be incremented.");
        assertTrue(quizState.getScore() > 0, "Score should be incremented.");
    }
}