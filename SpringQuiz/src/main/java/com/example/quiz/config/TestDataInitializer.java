package com.example.quiz.config;

import com.example.quiz.model.entity.*;
import com.example.quiz.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("test")
public class TestDataInitializer {
    private static final Logger logger = LoggerFactory.getLogger(TestDataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initializeTestData() {
        logger.info("Initializing test data");

        // Create test users
        createUsers();

        // Create test questions and answers
        createQuestions();

        logger.info("Test data initialization complete");
    }

    private void createUsers() {
        // Create test user
        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setEmail("test@example.com");

        // Create admin user
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setEmail("admin@example.com");
        adminUser.setRoles("ROLE_ADMIN");

        userRepository.save(testUser);
        userRepository.save(adminUser);

        logger.info("Created {} test users", userRepository.count());
    }

    private void createQuestions() {
        // Create a few test questions with answers
        for (int i = 1; i <= 10; i++) {
            Question question = new Question();
            question.setQuestionText("Test Question " + i);
            question.setDifficulty(i % 3 + 1); // Difficulty 1-3


            // One correct answer
            CorrectAnswer correctAnswer = new CorrectAnswer();
            correctAnswer.setAnswerText("Correct Answer for Q" + i);
            correctAnswer.setCorrect(true);
            correctAnswer.setQuestion(question);

            List<MockAnswer> mockAnswers = new ArrayList<>();
            // Three incorrect answers
            for (int j = 1; j <= 3; j++) {
                MockAnswer wrongAnswer = new MockAnswer();
                wrongAnswer.setAnswerText("Wrong Answer " + j + " for Q" + i);
                wrongAnswer.setCorrect(false);
                wrongAnswer.setQuestion(question);
                mockAnswers.add(wrongAnswer);
            }

            question.setCorrectAnswer(correctAnswer);
            question.setMockAnswers(mockAnswers);
            questionRepository.save(question);
        }

        logger.info("Created {} test questions", questionRepository.count());
    }
}