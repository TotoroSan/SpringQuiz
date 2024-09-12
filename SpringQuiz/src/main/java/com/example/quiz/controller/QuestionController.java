package com.example.quiz.controller;

import com.example.quiz.model.Question;
import com.example.quiz.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    // Get a question by ID
    @GetMapping("/{id}")
    public Optional<Question> getQuestionById(@PathVariable Long id) {
        return questionService.getQuestionById(id);
    }

    // Create a new question
    @PostMapping
    public Question createQuestion(@RequestBody Question question) {
        return questionService.createQuestion(question);
    }

    // Update a question
    @PutMapping("/{id}")
    public Question updateQuestion(@PathVariable Long id, @RequestBody Question questionDetails) {
        return questionService.updateQuestion(id, questionDetails);
    }

    // Delete a question
    @DeleteMapping("/{id}")
    public void deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
    }
}
