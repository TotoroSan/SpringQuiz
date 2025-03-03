package com.example.quiz.integrationTest.repository;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.LifeQuizModifierEffect.IncreaseLifeCounterQuizModifierEffect;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffectFactory;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.model.entity.User;
import com.example.quiz.repository.QuizModifierEffectRepository;
import com.example.quiz.repository.QuizModifierRepository;
import com.example.quiz.repository.QuizStateRepository;
import com.example.quiz.repository.UserRepository;
import com.example.quiz.service.user.UserQuizModifierService;
import com.example.quiz.service.user.UserQuizStateService;
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
    private UserQuizStateService quizStateService;

    @Autowired
    private QuizModifierEffectFactory quizModifierEffectFactory;

    @Autowired
    private UserQuizModifierService userQuizModifierService;

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


        QuizModifier quizModifier = testQuizState.getQuizModifier();
        quizModifier.setQuizState(testQuizState); // Associate with QuizState
        QuizModifier savedModifier = quizModifierRepository.save(quizModifier);

        // Create a concrete implementation of QuizModifierEffect
        IncreaseLifeCounterQuizModifierEffect effect = (IncreaseLifeCounterQuizModifierEffect) quizModifierEffectFactory.createEffect("INCREASE_LIFE_COUNTER", 1, quizModifier, 1);
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
        QuizModifier quizModifier = testQuizState.getQuizModifier();
        quizModifier.setQuizState(testQuizState); // Associate with QuizState
        QuizModifier savedModifier = quizModifierRepository.save(quizModifier);

        // Create and save a concrete effect
        IncreaseLifeCounterQuizModifierEffect effect = (IncreaseLifeCounterQuizModifierEffect) quizModifierEffectFactory.createEffect("INCREASE_LIFE_COUNTER", 1, quizModifier, 1);
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
        QuizModifier quizModifier = testQuizState.getQuizModifier();
        quizModifier.setQuizState(testQuizState); // Associate with QuizState
        QuizModifier savedModifier = quizModifierRepository.save(quizModifier);

        // Create and save a concrete effect
        IncreaseLifeCounterQuizModifierEffect effect = (IncreaseLifeCounterQuizModifierEffect) quizModifierEffectFactory.createEffect("INCREASE_LIFE_COUNTER", 1, quizModifier, 1);
        effect.setQuizModifier(savedModifier);

        QuizModifierEffect savedEffect = quizModifierEffectRepository.save(effect);
        Long id = savedEffect.getId();

        // Delete the effect
        quizModifierEffectRepository.delete(savedEffect);

        // Verify deletion
        assertFalse(quizModifierEffectRepository.existsById(id));
    }
}