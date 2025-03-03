// File: `src/test/java/com/example/quiz/repository/QuizSubmissionRepositoryTest.java`
package com.example.quiz.repository;

import com.example.quiz.model.entity.QuizSubmission;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class QuizSubmissionRepositoryTest {

    @Autowired
    private QuizSubmissionRepository quizSubmissionRepository;

    @Test
    public void testSaveAndFindQuizSubmission() {
        QuizSubmission submission = new QuizSubmission();
        submission.setUserId(1L);
        submission.setScore(10);
        QuizSubmission saved = quizSubmissionRepository.save(submission);
        assertNotNull(saved.getId());

        List<QuizSubmission> submissions = quizSubmissionRepository.findByUserId(1L);
        assertFalse(submissions.isEmpty());
    }
}