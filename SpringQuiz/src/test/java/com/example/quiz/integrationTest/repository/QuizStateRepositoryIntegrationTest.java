package com.example.quiz.integrationTest.repository;

import com.example.quiz.model.entity.QuizState;
import com.example.quiz.model.entity.User;
import com.example.quiz.repository.QuizStateRepository;
import com.example.quiz.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class QuizStateRepositoryIntegrationTest {

    @Autowired
    private     QuizStateRepository quizStateRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    public void setup() {
        // Create a test user
        User user = new User();
        user.setUsername("quizStateTestUser");
        user.setEmail("quizstate@example.com");
        user.setPassword("password");
        testUser = userRepository.save(user);
    }

    @Test
    public void testFindFirstByUserIdOrderByIdDesc() {
        // Create and save multiple quiz states for the same user
        QuizState state1 = createQuizState(testUser.getId(), false);
        QuizState state2 = createQuizState(testUser.getId(), false);

        // Test finding latest quiz state
        Optional<QuizState> latestState = quizStateRepository.findFirstByUserIdOrderByIdDesc(testUser.getId());
        assertTrue(latestState.isPresent());
        assertEquals(state2.getId(), latestState.get().getId());
    }

    @Test
    public void testFindFirstByUserIdAndIsActiveIsTrueOrderByIdDesc() {
        // Create inactive and active quiz states
        QuizState inactiveState = createQuizState(testUser.getId(), false);
        QuizState activeState = createQuizState(testUser.getId(), true);

        // Test finding latest active quiz state
        Optional<QuizState> latestActiveState =
                quizStateRepository.findFirstByUserIdAndIsActiveIsTrueOrderByIdDesc(testUser.getId());
        assertTrue(latestActiveState.isPresent());
        assertEquals(activeState.getId(), latestActiveState.get().getId());
        assertTrue(latestActiveState.get().isActive());
    }

    @Test
    public void testFindAllByUserIdAndIsActiveTrue() {
        // Clear previous states
        quizStateRepository.findByUserId(testUser.getId())
                .ifPresent(state -> quizStateRepository.delete(state));

        // Create multiple active quiz states
        QuizState activeState1 = createQuizState(testUser.getId(), true);
        QuizState activeState2 = createQuizState(testUser.getId(), true);
        QuizState inactiveState = createQuizState(testUser.getId(), false);

        // Test finding all active quiz states
        List<QuizState> activeStates = quizStateRepository.findAllByUserIdAndIsActiveTrue(testUser.getId());
        assertEquals(2, activeStates.size());
        assertTrue(activeStates.stream().allMatch(QuizState::isActive));
    }

    private QuizState createQuizState(Long userId, boolean isActive) {
        QuizState quizState = new QuizState();
        quizState.setUserId(userId);
        quizState.setActive(isActive);
        quizState.setScore(100);
        return quizStateRepository.save(quizState);
    }
}