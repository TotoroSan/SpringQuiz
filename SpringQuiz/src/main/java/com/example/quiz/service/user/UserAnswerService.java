package com.example.quiz.service.user;


import com.example.quiz.model.dto.AnswerDto;
import com.example.quiz.model.entity.Answer;
import com.example.quiz.model.entity.Question;
import com.example.quiz.repository.AnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public AnswerDto convertToDto(Answer answer){
        AnswerDto answerDto = new AnswerDto(answer.getId(), answer.getAnswerText());
        return answerDto;
    }
}
