package com.example.quiz.integrationTest.repository;

import com.example.quiz.model.entity.Question;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class QuestionRepositoryIntegrationTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void testSaveAndFindQuestion() {
        // Create a test question
        Question question = new Question();
        question.setQuestionText("What is JPA?");
        question.setDifficulty(2);
        question.setTopic("Java");

        // Save the entity
        Question savedQuestion = questionRepository.save(question);
        assertNotNull(savedQuestion.getId());

        // Retrieve the entity
        Question retrievedQuestion = questionRepository.findById(savedQuestion.getId()).orElse(null);
        assertNotNull(retrievedQuestion);
        assertEquals("What is JPA?", retrievedQuestion.getQuestionText());
        assertEquals(2, retrievedQuestion.getDifficulty());
        assertEquals("Java", retrievedQuestion.getTopic());
    }

    @Test
    public void testFindQuestionsByTopic() {
        // Create and save test questions with different topics
        Question question1 = new Question();
        question1.setQuestionText("Java Question");
        question1.setTopic("Java");
        question1.setDifficulty(1);
        questionRepository.save(question1);

        Question question2 = new Question();
        question2.setQuestionText("SQL Question");
        question2.setTopic("SQL");
        question2.setDifficulty(2);
        questionRepository.save(question2);

        // Test finding by topic
        Page<Question> javaQuestions = questionRepository.findQuestionsByTopic("Java", PageRequest.of(0, 10));
        assertTrue(javaQuestions.getContent().stream().allMatch(q -> "Java".equals(q.getTopic())));
    }

    @Test
    public void testFindRandomQuestionExcludingCompleted() {
        // Create and save multiple questions
        Question question1 = createQuestion("Q1", "Java", 1);
        Question question2 = createQuestion("Q2", "Java", 2);
        Question question3 = createQuestion("Q3", "SQL", 1);

        // Create a set of completed question IDs
        Set<Long> completedIds = new HashSet<>();
        completedIds.add(question1.getId());

        // Test finding random questions excluding completed ones
        Page<Question> randomQuestions = questionRepository.findRandomQuestionExcludingCompleted(
                completedIds, PageRequest.of(0, 10));

        // Verify that none of the returned questions are in the completedIds set
        assertTrue(randomQuestions.getContent().stream()
                .noneMatch(q -> completedIds.contains(q.getId())));
    }

    private Question createQuestion(String text, String topic, int difficulty) {
        Question question = new Question();
        question.setQuestionText(text);
        question.setTopic(topic);
        question.setDifficulty(difficulty);
        return questionRepository.save(question);
    }
}