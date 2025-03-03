package com.example.quiz.integrationTest.repository;

import com.example.quiz.model.entity.QuizSubmission;
import com.example.quiz.model.entity.User;
import com.example.quiz.repository.QuizSubmissionRepository;
import com.example.quiz.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class QuizSubmissionRepositoryIntegrationTest {

    @Autowired
    private QuizSubmissionRepository quizSubmissionRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    public void setup() {
        // Create a test user
        User user = new User();
        user.setUsername("submissionTestUser");
        user.setEmail("submission@example.com");
        user.setPassword("password");
        testUser = userRepository.save(user);
    }

    @Test
    public void testFindByUserId() {
        // Create submissions for the test user
        QuizSubmission submission1 = createSubmission(testUser.getId(), 80);
        QuizSubmission submission2 = createSubmission(testUser.getId(), 90);

        // Create a submission for another user
        User anotherUser = new User();
        anotherUser.setUsername("anotherUser");
        anotherUser.setEmail("another@example.com");
        anotherUser = userRepository.save(anotherUser);
        QuizSubmission otherSubmission = createSubmission(anotherUser.getId(), 70);

        // Test finding by user ID
        List<QuizSubmission> userSubmissions = quizSubmissionRepository.findByUserId(testUser.getId());
        assertEquals(2, userSubmissions.size());
        assertTrue(userSubmissions.stream().allMatch(sub -> sub.getUserId().equals(testUser.getId())));
    }

    private QuizSubmission createSubmission(Long userId, int score) {
        QuizSubmission submission = new QuizSubmission();
        submission.setUserId(userId);
        submission.setScore(score);
        return quizSubmissionRepository.save(submission);
    }
}