package com.example.quiz.service.admin;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.quiz.exception.ResourceNotFoundException;
import com.example.quiz.model.Answer;
import com.example.quiz.model.AnswerDto;
import com.example.quiz.model.Question;
import com.example.quiz.model.QuestionDto;
import com.example.quiz.model.QuestionWithShuffledAnswersDto;
import com.example.quiz.repository.QuestionRepository;

@Service
public class AdminQuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    public QuestionDto createQuestionFromDto(QuestionDto questionDto) {

        // create question model object
        Question question = new Question();

        List<Answer> mockAnswers = new ArrayList<>();

        // create mockAnswer objects
        for (String mockAnswer : questionDto.getMockAnswers()) {
        	mockAnswers.add(new Answer(mockAnswer, false, question));
        }

        // create real answer object
        Answer realAnswer = new Answer(questionDto.getRealAnswer(), true, question);

        // update question model object
        question.setQuestionText(questionDto.getQuestionText());
        question.setRealAnswer(realAnswer);
        question.setMockAnswers(mockAnswers);

        Question savedQuestion = questionRepository.save(question);

        return convertToDto(savedQuestion);
    }

    public QuestionDto updateQuestionFromDto(Long id, QuestionDto questionDto) {
        // Find the existing question by ID
        Question existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        // Update the question text
        existingQuestion.setQuestionText(questionDto.getQuestionText());

        // Update real answer
        Answer realAnswer = new Answer(questionDto.getRealAnswer(), true, existingQuestion);
        existingQuestion.setRealAnswer(realAnswer);

        // Update mock answers
        List<Answer> mockAnswers = questionDto.getMockAnswers()
                .stream()
                .map(mockAnswerText -> new Answer(mockAnswerText, false, existingQuestion))
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
        String realAnswerText = question.getRealAnswer().getAnswerText();

        // extract mockAnswer text as string from answer objects
        List<String> mockAnswersText = question.getMockAnswers()
                                               .stream()
                                               .map(Answer::getAnswerText)
                                               .collect(Collectors.toList());

        QuestionDto questionDto = new QuestionDto();
        questionDto.setQuestionText(question.getQuestionText());
        questionDto.setRealAnswer(realAnswerText);
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
    // Get a random question
	public QuestionWithShuffledAnswersDto getRandomQuestionWithShuffledAnswers() {
		// TODO check where to add exception for findRandomQuestion (e.g. there are no questions left)
		// we use this datatype and not questionDto because we do not want to reveal real true answer to the client

		// get random question from repository
		Question question = questionRepository.findRandomQuestion();

		// Prepare a list to hold the final answers (including the real one)
        List<AnswerDto> finalAnswers = new ArrayList<>();

        // Add the correct answer
        finalAnswers.add(new AnswerDto(question.getRealAnswer().getId(), question.getRealAnswer().getAnswerText()));

        // Create a copy of the mock answers list to shuffle
        List<Answer> mockAnswersCopy = new ArrayList<>(question.getMockAnswers());
        Collections.shuffle(mockAnswersCopy);  // Shuffle the copied list

        // Add up to 3 mock answers from the shuffled copy
        for (int i = 0; i < Math.min(3,mockAnswersCopy.size()); i++) {
        	finalAnswers.add(new AnswerDto(mockAnswersCopy.get(i).getId(), mockAnswersCopy.get(i).getAnswerText()));
        }

        // Shuffle the final list of answers (real + selected mock answers)
        Collections.shuffle(finalAnswers);

        // Return the question with the shuffled answers
        return new QuestionWithShuffledAnswersDto(question.getQuestionText(), finalAnswers);
	}
}
