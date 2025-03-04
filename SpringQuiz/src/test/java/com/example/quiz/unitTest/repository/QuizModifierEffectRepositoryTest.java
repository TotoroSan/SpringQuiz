// File: `src/test/java/com/example/quiz/repository/QuizModifierEffectRepositoryTest.java`
package com.example.quiz.unitTest.repository;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.LifeQuizModifierEffect.IncreaseLifeCounterQuizModifierEffect;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffectFactory;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.repository.QuizModifierEffectRepository;
import com.example.quiz.repository.QuizModifierRepository;
import com.example.quiz.repository.QuizStateRepository;
import com.example.quiz.service.user.UserQuizModifierService;
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

    @Autowired
    private UserQuizModifierService userQuizModifierService;

    @Autowired
    private QuizModifierEffectFactory quizModifierEffectFactory;

    @Test
    public void testSaveAndFindQuizModifierEffect() {
        QuizState quizState = new QuizState();
        quizState.setUserId(1L);
        quizState.setActive(true);
        QuizState savedState = quizStateRepository.save(quizState);

        QuizModifier modifier = quizState.getQuizModifier();
        QuizModifier savedModifier = quizModifierRepository.save(modifier);

        IncreaseLifeCounterQuizModifierEffect effect = (IncreaseLifeCounterQuizModifierEffect) quizModifierEffectFactory.createEffect("INCREASE_LIFE_COUNTER", 1, savedModifier, 1);
        effect.setQuizModifier(savedModifier);
        QuizModifierEffect savedEffect = quizModifierEffectRepository.save(effect);
        assertNotNull(savedEffect.getId());

        QuizModifierEffect found = quizModifierEffectRepository.findById(savedEffect.getId()).orElse(null);
        assertNotNull(found);
        assertTrue(found instanceof IncreaseLifeCounterQuizModifierEffect);
        assertEquals(savedModifier.getId(), found.getQuizModifier().getId());
    }
}