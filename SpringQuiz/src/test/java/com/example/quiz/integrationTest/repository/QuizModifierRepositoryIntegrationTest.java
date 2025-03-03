package com.example.quiz.integrationTest.repository;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class QuizModifierRepositoryIntegrationTest {

    @Autowired
    private QuizModifierRepository quizModifierRepository;

    @Autowired
    private QuizStateRepository quizStateRepository;

    @Autowired
    private UserRepository userRepository;

    private QuizState testQuizState;

    @BeforeEach
    public void setup() {
        // Create a test user
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        User savedUser = userRepository.save(user);

        // Create and save a test quiz state using the saved user
        QuizState quizState = new QuizState(savedUser.getId());
        quizState.setActive(true);
        testQuizState = quizStateRepository.save(quizState);
    }

    @Test
    public void testSaveAndFindQuizModifier() {
        // Create a QuizModifier associated with the testQuizState
        QuizModifier quizModifier = new QuizModifier(testQuizState);
        quizModifier.setScoreMultiplier(1.5);
        quizModifier.setDifficultyModifier(2);

        // Save the QuizModifier
        QuizModifier savedModifier = quizModifierRepository.save(quizModifier);
        assertNotNull(savedModifier.getId());

        // Retrieve from repository and check properties
        QuizModifier retrievedModifier = quizModifierRepository.findById(savedModifier.getId()).orElse(null);
        assertNotNull(retrievedModifier);
        assertEquals(1.5, retrievedModifier.getScoreMultiplier(), 0.001);
        assertEquals(2, retrievedModifier.getDifficultyModifier());
        assertEquals(testQuizState.getId(), retrievedModifier.getQuizState().getId());
    }

    @Test
    public void testUpdateQuizModifier() {
        // Create and save a QuizModifier
        QuizModifier quizModifier = new QuizModifier(testQuizState);
        quizModifier.setScoreMultiplier(1.0);
        quizModifier.setDifficultyModifier(1);
        QuizModifier savedModifier = quizModifierRepository.save(quizModifier);

        // Update properties
        savedModifier.setScoreMultiplier(2.0);
        savedModifier.setDifficultyModifier(3);
        quizModifierRepository.save(savedModifier);

        // Verify update
        QuizModifier updatedModifier = quizModifierRepository.findById(savedModifier.getId()).orElse(null);
        assertNotNull(updatedModifier);
        assertEquals(2.0, updatedModifier.getScoreMultiplier(), 0.001);
        assertEquals(3, updatedModifier.getDifficultyModifier());
    }

    @Test
    public void testDeleteQuizModifier() {
        // Create and save a QuizModifier
        QuizModifier quizModifier = new QuizModifier(testQuizState);
        quizModifier.setScoreMultiplier(1.0);
        QuizModifier savedModifier = quizModifierRepository.save(quizModifier);
        Long id = savedModifier.getId();

        // Delete the entity
        quizModifierRepository.delete(savedModifier);

        // Verify deletion
        assertFalse(quizModifierRepository.existsById(id));
    }
}