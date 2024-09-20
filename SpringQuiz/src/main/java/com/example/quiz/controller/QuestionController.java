package com.example.quiz.controller;

import com.example.quiz.model.Question;
import com.example.quiz.model.QuestionDto;
import com.example.quiz.model.QuestionWithShuffledAnswersDto;
import com.example.quiz.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    // Get a question by ID TODO maybe change to optional return type because of possible null value or figure out how to handle this 
    @GetMapping("/{id}")
    public QuestionDto getQuestionById(@PathVariable Long id) {
        return questionService.getQuestionById(id);
    }
    
    // Get a random question with shuffled answers including realAnswer and answers from the mock-answer pool
    @GetMapping
    public QuestionWithShuffledAnswersDto getRandomQuestionWithShuffledAnswers() {
        return questionService.getRandomQuestionWithShuffledAnswers();
    }

    // Create a new question
    @PostMapping
    public QuestionDto createQuestion(@RequestBody QuestionDto questionDto) {
        return questionService.createQuestionFromDto(questionDto);
    }

    // Update a question
    @PutMapping("/{id}")
    public QuestionDto updateQuestion(@PathVariable Long id, @RequestBody QuestionDto questionDto) {
        return questionService.updateQuestionFromDto(id, questionDto);
    }

    // Delete a question
    @DeleteMapping("/{id}")
    public void deleteQuestionById(@PathVariable Long id) {
        questionService.deleteQuestionById(id);
    }
    
}
