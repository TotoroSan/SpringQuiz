package com.example.quiz.unitTest.repository;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.repository.QuizModifierRepository;
import com.example.quiz.repository.QuizStateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class QuizModifierRepositoryTest {

    @Autowired
    private QuizModifierRepository quizModifierRepository;

    @Autowired
    private QuizStateRepository quizStateRepository;

    @Test
    public void testSaveAndFindQuizModifier() {
        QuizState quizState = new QuizState();
        quizState.setUserId(1L);
        quizState.setActive(true);
        QuizState savedState = quizStateRepository.save(quizState);

        QuizModifier modifier = quizState.getQuizModifier();
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