package com.example.quiz.service.user;


import com.example.quiz.exception.ResourceNotFoundException;
import com.example.quiz.model.Answer;
import com.example.quiz.model.AnswerDto;
import com.example.quiz.model.Question;
import com.example.quiz.repository.AnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserAnswerService {
	// for future use
	
    @Autowired
    private AnswerRepository answerRepository;

    // check if answer is true 
	public Boolean isCorrectAnswer(AnswerDto answerDto, Question question) {
		// check answerId against the corresponding question
		return answerDto.getId().equals(question.getCorrectAnswer().getId()); 	
	}
}
