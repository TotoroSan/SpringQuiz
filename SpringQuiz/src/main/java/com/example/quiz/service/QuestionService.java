package com.example.quiz.service;


import com.example.quiz.exception.ResourceNotFoundException;
import com.example.quiz.model.Question;
import com.example.quiz.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    // Create a new Question
    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }

    // Get a question by its ID
    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }

    // Update an existing question
    public Question updateQuestion(Long id, Question questionDetails) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found for this id :: " + id));

        question.setQuestionText(questionDetails.getQuestionText());
        question.setAnswers(questionDetails.getAnswers());

        return questionRepository.save(question);
    }

    // Delete a question
    public void deleteQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found for this id :: " + id));
        
        questionRepository.delete(question);
    }
}
