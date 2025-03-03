// File: `src/test/java/com/example/quiz/repository/QuizModifierEffectRepositoryTest.java`
package com.example.quiz.unitTest.repository;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.LifeQuizModifierEffect.IncreaseLifeCounterQuizModifierEffect;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import com.example.quiz.model.entity.QuizState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class QuizModifierEffectRepositoryTest {

    @Autowired
    private QuizModifierEffectRepository quizModifierEffectRepository;

    @Autowired
    private QuizModifierRepository quizModifierRepository;

    @Autowired
    private QuizStateRepository quizStateRepository;

    @Test
    public void testSaveAndFindQuizModifierEffect() {
        QuizState state = new QuizState();
        state.setUserId(1L);
        state.setActive(true);
        QuizState savedState = quizStateRepository.save(state);

        QuizModifier modifier = new QuizModifier(savedState);
        QuizModifier savedModifier = quizModifierRepository.save(modifier);

        IncreaseLifeCounterQuizModifierEffect effect = new IncreaseLifeCounterQuizModifierEffect();
        effect.setQuizModifier(savedModifier);
        QuizModifierEffect savedEffect = quizModifierEffectRepository.save(effect);
        assertNotNull(savedEffect.getId());

        QuizModifierEffect found = quizModifierEffectRepository.findById(savedEffect.getId()).orElse(null);
        assertNotNull(found);
        assertTrue(found instanceof IncreaseLifeCounterQuizModifierEffect);
        assertEquals(savedModifier.getId(), found.getQuizModifier().getId());
    }
}