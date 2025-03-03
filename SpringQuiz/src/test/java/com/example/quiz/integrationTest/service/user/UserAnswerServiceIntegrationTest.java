package com.example.quiz.integrationTest.service.user;

import com.example.quiz.model.dto.AnswerDto;
import com.example.quiz.model.entity.Answer;
import com.example.quiz.model.entity.CorrectAnswer;
import com.example.quiz.model.entity.Question;
import com.example.quiz.repository.AnswerRepository;
import com.example.quiz.service.user.UserAnswerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserAnswerServiceIntegrationTest {

    @Autowired
    private UserAnswerService userAnswerService;

    @Autowired
    private AnswerRepository answerRepository;

    @Test
    void testConvertToDto() {
        // Create and persist an Answer entity.
        Answer answer = new CorrectAnswer();
        answer.setAnswerText("Sample Answer");
        answer = answerRepository.save(answer);

        // Convert to DTO using the service.
        AnswerDto answerDto = userAnswerService.convertToDto(answer);
        assertNotNull(answerDto);
        assertEquals(answer.getId(), answerDto.getId());
        assertEquals("Sample Answer", answerDto.getText());
    }

    @Test
    void testIsCorrectAnswer() {
        // Create and persist an Answer that will serve as the correct answer.
        CorrectAnswer correctAnswer = new CorrectAnswer();
        correctAnswer.setAnswerText("Correct Answer");
        correctAnswer = answerRepository.save(correctAnswer);

        // Create a Question and assign the correct Answer.
        Question question = new Question();
        question.setCorrectAnswer(correctAnswer);

        // Create a matching AnswerDto.
        AnswerDto correctDto = new AnswerDto(correctAnswer.getId(), "Correct Answer");
        assertTrue(userAnswerService.isCorrectAnswer(correctDto, question));

        // Create an AnswerDto with a mismatching ID.
        AnswerDto incorrectDto = new AnswerDto(correctAnswer.getId() + 1, "Incorrect Answer");
        assertFalse(userAnswerService.isCorrectAnswer(incorrectDto, question));
    }
}