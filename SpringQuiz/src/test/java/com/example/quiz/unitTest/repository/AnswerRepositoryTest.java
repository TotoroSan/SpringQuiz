package com.example.quiz.unitTest.repository;

import com.example.quiz.model.entity.Answer;
import com.example.quiz.model.entity.CorrectAnswer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class AnswerRepositoryTest {

    @Autowired
    private AnswerRepository answerRepository;

    @Test
    public void testSaveAndFindAnswer() {
        Answer answer = new CorrectAnswer();
        answer.setAnswerText("Sample answer text");
        Answer saved = answerRepository.save(answer);
        assertNotNull(saved.getId());

        Answer found = answerRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("Sample answer text", found.getAnswerText());
    }
}