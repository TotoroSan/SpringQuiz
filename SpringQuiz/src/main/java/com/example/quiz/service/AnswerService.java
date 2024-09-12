package com.example.quiz.service;


import com.example.quiz.exception.ResourceNotFoundException;
import com.example.quiz.model.Answer;
import com.example.quiz.repository.AnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnswerService {

    @Autowired
    private AnswerRepository answerRepository;

    // Create a new Answer
    public Answer createAnswer(Answer answer) {
        return answerRepository.save(answer);
    }

    // Get an answer by its ID
    public Optional<Answer> getAnswerById(Long id) {
        return answerRepository.findById(id);
    }

    // Update an existing answer
    public Answer updateAnswer(Long id, Answer answerDetails) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found for this id :: " + id));

        answer.setAnswerText(answerDetails.getAnswerText());
        answer.setCorrect(answerDetails.isCorrect());

        return answerRepository.save(answer);
    }

    // Delete an answer
    public void deleteAnswer(Long id) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found for this id :: " + id));
        
        answerRepository.delete(answer);
    }
}
