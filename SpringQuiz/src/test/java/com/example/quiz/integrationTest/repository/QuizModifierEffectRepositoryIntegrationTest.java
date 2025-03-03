package com.example.quiz.integrationTest.repository;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.LifeQuizModifierEffect.IncreaseLifeCounterQuizModifierEffect;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
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
public class QuizModifierEffectRepositoryIntegrationTest {

    @Autowired
    private QuizModifierEffectRepository quizModifierEffectRepository;

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

        // Create a test quiz state
        QuizState quizState = new QuizState();
        quizState.setUserId(savedUser.getId());
        quizState.setActive(true);
        testQuizState = quizStateRepository.save(quizState);
    }


    @Test
    public void testSaveAndFindQuizModifierEffect() {
        // Create a quiz modifier and associate it with the QuizState
        QuizModifier quizModifier = new QuizModifier(testQuizState);
        quizModifier.setQuizState(testQuizState); // Associate with QuizState
        QuizModifier savedModifier = quizModifierRepository.save(quizModifier);

        // Create a concrete implementation of QuizModifierEffect
        IncreaseLifeCounterQuizModifierEffect effect = new IncreaseLifeCounterQuizModifierEffect();
        effect.setQuizModifier(savedModifier);

        // Save the entity
        QuizModifierEffect savedEffect = quizModifierEffectRepository.save(effect);
        assertNotNull(savedEffect.getId());

        // Retrieve the entity by ID
        QuizModifierEffect retrievedEffect = quizModifierEffectRepository.findById(savedEffect.getId()).orElse(null);
        assertNotNull(retrievedEffect);
        assertTrue(retrievedEffect instanceof IncreaseLifeCounterQuizModifierEffect);

        IncreaseLifeCounterQuizModifierEffect typedEffect = (IncreaseLifeCounterQuizModifierEffect) retrievedEffect;
        assertEquals(savedModifier.getId(), typedEffect.getQuizModifier().getId());
    }

    @Test
    public void testUpdateQuizModifierEffect() {
        // Create a quiz modifier and associate it with the QuizState
        QuizModifier quizModifier = new QuizModifier(testQuizState);
        quizModifier.setQuizState(testQuizState); // Associate with QuizState
        QuizModifier savedModifier = quizModifierRepository.save(quizModifier);

        // Create and save a concrete effect
        IncreaseLifeCounterQuizModifierEffect effect = new IncreaseLifeCounterQuizModifierEffect();
        effect.setQuizModifier(savedModifier);

        QuizModifierEffect savedEffect = quizModifierEffectRepository.save(effect);

        // Update the effect
        IncreaseLifeCounterQuizModifierEffect toUpdate = (IncreaseLifeCounterQuizModifierEffect) savedEffect;
        quizModifierEffectRepository.save(toUpdate);

        // Verify update
        QuizModifierEffect updatedEffect = quizModifierEffectRepository.findById(savedEffect.getId()).orElse(null);
        assertNotNull(updatedEffect);
    }

    @Test
    public void testDeleteQuizModifierEffect() {
        // Create a quiz modifier and associate it with the QuizState
        QuizModifier quizModifier =  new QuizModifier(testQuizState);
        quizModifier.setQuizState(testQuizState); // Associate with QuizState
        QuizModifier savedModifier = quizModifierRepository.save(quizModifier);

        // Create and save a concrete effect
        IncreaseLifeCounterQuizModifierEffect effect = new IncreaseLifeCounterQuizModifierEffect();
        effect.setQuizModifier(savedModifier);

        QuizModifierEffect savedEffect = quizModifierEffectRepository.save(effect);
        Long id = savedEffect.getId();

        // Delete the effect
        quizModifierEffectRepository.delete(savedEffect);

        // Verify deletion
        assertFalse(quizModifierEffectRepository.existsById(id));
    }
}