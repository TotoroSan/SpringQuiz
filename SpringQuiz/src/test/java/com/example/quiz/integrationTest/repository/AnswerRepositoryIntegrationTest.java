package com.example.quiz.integrationTest.repository;

import com.example.quiz.model.entity.Answer;
import com.example.quiz.model.entity.CorrectAnswer;
import com.example.quiz.model.entity.MockAnswer;
import com.example.quiz.model.entity.Question;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class AnswerRepositoryIntegrationTest {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void testSaveAndFindAnswer() {
        // Create a test question
        Question question = new Question();
        question.setQuestionText("Test Question");
        question.setDifficulty(1);
        Question savedQuestion = questionRepository.save(question);

        // Create a test answer
        Answer answer = new CorrectAnswer();
        answer.setAnswerText("Test Answer");
        answer.setCorrect(true);
        answer.setQuestion(savedQuestion);

        // Save the answer
        Answer savedAnswer = answerRepository.save(answer);
        assertNotNull(savedAnswer.getId());

        // Find the answer by ID
        Answer foundAnswer = answerRepository.findById(savedAnswer.getId()).orElse(null);
        assertNotNull(foundAnswer);
        assertEquals("Test Answer", foundAnswer.getAnswerText());
        assertTrue(foundAnswer.isCorrect());
    }

    @Test
    public void testUpdateAnswer() {
        // Create a question
        Question question = new Question();
        question.setQuestionText("Update Test Question");
        question.setDifficulty(1);
        Question savedQuestion = questionRepository.save(question);

        // Create and save an answer
        Answer answer = new CorrectAnswer();
        answer.setAnswerText("Initial Answer");
        answer.setCorrect(false);
        answer.setQuestion(savedQuestion);

        Answer savedAnswer = answerRepository.save(answer);

        // Update the answer
        savedAnswer.setAnswerText("Updated Answer");
        savedAnswer.setCorrect(true);
        answerRepository.save(savedAnswer);

        // Find and verify update
        Answer updatedAnswer = answerRepository.findById(savedAnswer.getId()).orElse(null);
        assertNotNull(updatedAnswer);
        assertEquals("Updated Answer", updatedAnswer.getAnswerText());
        assertTrue(updatedAnswer.isCorrect());
    }

    @Test
    public void testDeleteAnswer() {
        // Create a question
        Question question = new Question();
        question.setQuestionText("Delete Test Question");
        question.setDifficulty(1);
        Question savedQuestion = questionRepository.save(question);

        // Create and save an answer
        Answer answer = new MockAnswer();
        answer.setAnswerText("Answer to Delete");
        answer.setCorrect(true);
        answer.setQuestion(savedQuestion);

        Answer savedAnswer = answerRepository.save(answer);
        Long id = savedAnswer.getId();

        // Delete the answer
        answerRepository.delete(savedAnswer);

        // Verify deletion
        assertFalse(answerRepository.existsById(id));
    }

    @Test
    public void testBatchOperations() {
        // Create a question
        Question question = new Question();
        question.setQuestionText("Batch Test Question");
        question.setDifficulty(2);
        Question savedQuestion = questionRepository.save(question);

        // Create multiple answers
        Answer answer1 = new MockAnswer();
        answer1.setAnswerText("First Answer");
        answer1.setCorrect(true);
        answer1.setQuestion(savedQuestion);

        Answer answer2 = new MockAnswer();
        answer2.setAnswerText("Second Answer");
        answer2.setCorrect(false);
        answer2.setQuestion(savedQuestion);

        // Save all answers
        List<Answer> savedAnswers = answerRepository.saveAll(List.of(answer1, answer2));
        assertEquals(2, savedAnswers.size());

        // Find all answers
        List<Answer> allAnswers = answerRepository.findAll();
        assertTrue(allAnswers.size() >= 2);
    }
}