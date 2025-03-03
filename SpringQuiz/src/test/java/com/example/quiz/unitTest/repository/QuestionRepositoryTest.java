package com.example.quiz.unitTest.repository;

import com.example.quiz.model.entity.Question;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void testFindRandomQuestion() {
        // Save a sample question so the query returns result
        Question question = new Question();
        question.setTopic("Science");
        question.setDifficulty(1);
        question.setQuestionText("What is the boiling point of water?");
        questionRepository.save(question);

        Question random = questionRepository.findRandomQuestion();
        assertNotNull(random);
    }
}