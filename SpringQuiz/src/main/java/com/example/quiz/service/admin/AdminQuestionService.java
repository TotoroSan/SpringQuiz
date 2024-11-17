package com.example.quiz.service.admin;


import com.example.quiz.exception.ResourceNotFoundException;
import com.example.quiz.model.dto.QuestionDto;
import com.example.quiz.model.entity.Answer;
import com.example.quiz.model.entity.CorrectAnswer;
import com.example.quiz.model.entity.MockAnswer;
import com.example.quiz.model.entity.Question;
import com.example.quiz.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminQuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @SuppressWarnings("deprecation")
	public QuestionDto createQuestionFromDto(QuestionDto questionDto) {

        // create question model object
        Question question = new Question();

        List<MockAnswer> mockAnswers = new ArrayList<>();

        // create mockAnswer objects
        for (String mockAnswer : questionDto.getMockAnswers()) {
        	mockAnswers.add(new MockAnswer(mockAnswer, question));
        }
        
        // create real answer object
        CorrectAnswer correctAnswer = new CorrectAnswer(questionDto.getRealAnswer(),question);

        // update question model object
        question.setQuestionText(questionDto.getQuestionText());
        question.setCorrectAnswer(correctAnswer);
        question.setMockAnswers(mockAnswers);
        
        // DEBUG: Print out mock answers and correct answer before saving
        System.out.println("Correct Answer BEFORE SAVING: " + question.getCorrectAnswer().getAnswerText());
        for (Answer mockAnswer : question.getMockAnswers()) {
            System.out.println("Mock Answer: " + mockAnswer.getAnswerText());
        }

        Question savedQuestion = questionRepository.save(question);
          
        // DEBUG: Print out mock answers and correct answer before saving
        System.out.println("Correct Answer AFTER SAVING: " + savedQuestion.getCorrectAnswer().getAnswerText());
        for (Answer mockAnswer : savedQuestion.getMockAnswers()) {
            System.out.println("Mock Answer: " + mockAnswer.getAnswerText());
        }
        
        savedQuestion = questionRepository.getById(savedQuestion.getId());
        
        // DEBUG: Print out mock answers and correct answer before saving
        System.out.println("Correct Answer AFTER RETREIVING: " + savedQuestion.getCorrectAnswer().getAnswerText());
        for (Answer mockAnswer : savedQuestion.getMockAnswers()) {
            System.out.println("Mock Answer: " + mockAnswer.getAnswerText());
        }
        
        
        return convertToDto(savedQuestion);
    }

    public QuestionDto updateQuestionFromDto(Long id, QuestionDto questionDto) {
        // Find the existing question by ID
        Question existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        // Update the question text
        existingQuestion.setQuestionText(questionDto.getQuestionText());

        // Update real answer
        CorrectAnswer correctAnswer = new CorrectAnswer(questionDto.getRealAnswer(), existingQuestion);
        existingQuestion.setCorrectAnswer(correctAnswer);

        // Update mock answers
        List<MockAnswer> mockAnswers = questionDto.getMockAnswers()
                .stream()
                .map(mockAnswerText -> new MockAnswer(mockAnswerText, existingQuestion))
                .collect(Collectors.toList());
        existingQuestion.setMockAnswers(mockAnswers);

        // Save updated question
        Question updatedQuestion = questionRepository.save(existingQuestion);

        // Return the updated question as DTO
        return convertToDto(updatedQuestion);
    }

    public QuestionDto getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        return convertToDto(question);
    }

    public QuestionDto convertToDto(Question question) {
        String correctAnswerText = question.getCorrectAnswer().getAnswerText();

        // extract mockAnswer text as string from answer objects
        List<String> mockAnswersText = question.getMockAnswers()
                                               .stream()
                                               .map(Answer::getAnswerText)
                                               .collect(Collectors.toList());

        QuestionDto questionDto = new QuestionDto();
        questionDto.setQuestionText(question.getQuestionText());
        questionDto.setRealAnswer(correctAnswerText);
        questionDto.setMockAnswers(mockAnswersText);

        return questionDto;
    }
    public void deleteQuestionById(Long id) {
        // Check if the question exists before deleting
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        // Delete the question
        questionRepository.delete(question);
    }

}
