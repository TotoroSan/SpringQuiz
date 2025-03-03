package com.example.quiz.unitTest.repository;

import com.example.quiz.model.entity.Quiz;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class QuizRepositoryTest {

    @Autowired
    private QuizRepository quizRepository;

    @Test
    public void testSaveAndFindQuiz() {
        Quiz quiz = new Quiz();
        quiz.setTitle("Sample Quiz");
        Quiz saved = quizRepository.save(quiz);
        assertNotNull(saved.getId());

        Quiz found = quizRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("Sample Quiz", found.getTitle());
    }
}