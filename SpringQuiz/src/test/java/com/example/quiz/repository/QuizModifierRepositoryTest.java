// File: `src/test/java/com/example/quiz/repository/QuizModifierRepositoryTest.java`
package com.example.quiz.repository;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class QuizModifierRepositoryTest {

    @Autowired
    private QuizModifierRepository quizModifierRepository;

    @Autowired
    private QuizStateRepository quizStateRepository;

    @Test
    public void testSaveAndFindQuizModifier() {
        QuizState state = new QuizState();
        state.setUserId(1L);
        state.setActive(true);
        QuizState savedState = quizStateRepository.save(state);

        QuizModifier modifier = new QuizModifier(savedState);
        // Set additional properties if present
        modifier.setScoreMultiplier(1.0);
        QuizModifier savedModifier = quizModifierRepository.save(modifier);
        assertNotNull(savedModifier.getId());

        QuizModifier found = quizModifierRepository.findById(savedModifier.getId()).orElse(null);
        assertNotNull(found);
        assertEquals(1.0, found.getScoreMultiplier(), 0.001);
        assertEquals(savedState.getId(), found.getQuizState().getId());
    }
}