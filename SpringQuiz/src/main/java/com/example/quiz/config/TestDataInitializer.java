// File: src/main/java/com/example/quiz/config/TestDataInitializer.java
package com.example.quiz.config;

import com.example.quiz.model.entity.CorrectAnswer;
import com.example.quiz.model.entity.MockAnswer;
import com.example.quiz.model.entity.Question;
import com.example.quiz.model.entity.User;
import com.example.quiz.model.entity.UserProfile;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.GameEvent;
import com.example.quiz.model.entity.QuestionGameEvent;
import com.example.quiz.model.enums.GameEventType;
import com.example.quiz.repository.AnswerRepository;
import com.example.quiz.repository.QuestionRepository;
import com.example.quiz.repository.UserRepository;
import com.example.quiz.repository.QuizStateRepository;
import com.example.quiz.repository.QuizModifierRepository;
import com.example.quiz.repository.GameEventRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
    private QuizStateRepository quizStateRepository;

    @Autowired
    private QuizModifierRepository quizModifierRepository;

    @Autowired
    private GameEventRepository gameEventRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initializeTestData() {
        logger.info("Initializing test data");

        createUsers();
        createQuestions();
        createQuizData();
        createGameEvents();

        logger.info("Test data initialization complete");
    }

    // Create test users with associated profiles.
    private void createUsers() {
        // Create test user with profile
        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setEmail("test@example.com");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setRoles("ROLE_USER");

        UserProfile testProfile = new UserProfile();
        testProfile.setFirstName("Test");
        testProfile.setLastName("User");
        testProfile.setEmail("testprofile@example.com");
        testProfile.setBio("Test user profile");
        testUser.setUserProfile(testProfile);

        // Create admin user with profile
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setEmail("admin@example.com");
        adminUser.setCreatedAt(LocalDateTime.now());
        adminUser.setRoles("ROLE_ADMIN");

        UserProfile adminProfile = new UserProfile();
        adminProfile.setFirstName("Admin");
        adminProfile.setLastName("User");
        adminProfile.setEmail("adminprofile@example.com");
        adminProfile.setBio("Admin user profile");
        adminUser.setUserProfile(adminProfile);

        userRepository.save(testUser);
        userRepository.save(adminUser);

        logger.info("Created {} test users", userRepository.count());
    }

    // Create test questions with correct and mock answers.
    private void createQuestions() {
        for (int i = 1; i <= 10; i++) {
            Question question = new Question();
            question.setQuestionText("Test Question " + i);
            question.setDifficulty(i % 3 + 1);
            question.setTopic("Topic " + ((i % 5) + 1));

            // Create and set the correct answer.
            CorrectAnswer correctAnswer = new CorrectAnswer();
            correctAnswer.setAnswerText("Correct Answer for Q" + i);
            correctAnswer.setCorrect(true);
            correctAnswer.setQuestion(question);

            // Create mock answers.
            List<MockAnswer> mockAnswers = new ArrayList<>();
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

    // Create quiz state and quiz modifier data for users.
    private void createQuizData() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            // Create a quiz state for each user.
            QuizState quizState = new QuizState(user.getId());
            quizState.setActive(true);
            QuizState savedState = quizStateRepository.save(quizState);

            // Create a quiz modifier and associate it with the quiz state.
            QuizModifier quizModifier = quizState.getQuizModifier();
            quizModifier.setScoreMultiplier(1.0 + (Math.random() * 2)); // Random multiplier between 1 and 3
            quizModifier.setDifficultyModifier((int) (1 + (Math.random() * 3))); // Random difficulty modifier between 1 and 3
            quizModifier.setQuizState(savedState);
            quizModifierRepository.save(quizModifier);
        }
        logger.info("Created quiz state and modifier data, count: {}", quizStateRepository.count());
    }

    // Create game events including concrete QuestionGameEvent instances.
    private void createGameEvents() {
        // Create a few game events referencing question IDs.
        for (int i = 1; i <= 5; i++) {
            QuestionGameEvent gameEvent = new QuestionGameEvent();
            gameEvent.setGameEventType(GameEventType.QUESTION);
            gameEvent.setQuestionId((long) i);
            gameEventRepository.save(gameEvent);
        }
        logger.info("Created {} game events", gameEventRepository.count());
    }
}