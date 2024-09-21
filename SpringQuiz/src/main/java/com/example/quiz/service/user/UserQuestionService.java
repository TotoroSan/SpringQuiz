package com.example.quiz.service.user;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.quiz.exception.ResourceNotFoundException;
import com.example.quiz.model.Answer;
import com.example.quiz.model.AnswerDto;
import com.example.quiz.model.MockAnswer;
import com.example.quiz.model.Question;
import com.example.quiz.model.QuestionDto;
import com.example.quiz.model.QuestionWithShuffledAnswersDto;
import com.example.quiz.repository.QuestionRepository;

@Service
public class UserQuestionService {

    @Autowired
    private QuestionRepository questionRepository;
    

    public QuestionDto convertToDto(Question question) {
        String realAnswerText = question.getCorrectAnswer().getAnswerText();

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

    // Get a random question
	public Question getRandomQuestion() {
		// get random question from repository
		Question question = questionRepository.findRandomQuestion();

        // Return the question
        return question; 
	}
	
    // Get a Question with Shuffled Answers
	public QuestionWithShuffledAnswersDto createQuestionWithShuffledAnswersDto(Question question) {
		// TODO check where to add exception for findRandomQuestion (e.g. there are no questions left)
		// we use this datatype and not questionDto because we do not want to reveal real true answer to the client

		// Prepare a list to hold the final answers (including the real one)
        List<AnswerDto> finalAnswers = new ArrayList<>();

        // Add the correct answer
        finalAnswers.add(new AnswerDto(question.getCorrectAnswer().getId(), question.getCorrectAnswer().getAnswerText()));
        // Debug
        System.out.println("Non copy before shuffle: " + question.getMockAnswers());
        // Create a copy of the mock answers list to shuffle
        List<MockAnswer> mockAnswersCopy = new ArrayList<>(question.getMockAnswers());
        
        

        // Debug
        System.out.println("Mock answers before shuffle: " + mockAnswersCopy);
        
        Collections.shuffle(mockAnswersCopy);  // Shuffle the copied list
        
        // Debug
        System.out.println("Mock answers after shuffle: " + mockAnswersCopy);

        // Add up to 3 mock answers from the shuffled copy
        for (int i = 0; i < Math.min(3, mockAnswersCopy.size()); i++) {
        	finalAnswers.add(new AnswerDto(mockAnswersCopy.get(i).getId(), mockAnswersCopy.get(i).getAnswerText()));
        }

        // Shuffle the final list of answers (real + selected mock answers)
        Collections.shuffle(finalAnswers);

        // Return the question with the shuffled answers
        return new QuestionWithShuffledAnswersDto(question.getQuestionText(), question.getId(), finalAnswers);
	}
	

	
	
	
}
