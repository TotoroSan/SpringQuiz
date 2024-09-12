package com.example.quiz.controller;


import com.example.quiz.model.Quiz;
import com.example.quiz.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// having the same URI for different actions (like create, update, and delete) but distinguishing them by the HTTP method (POST, PUT, DELETE, etc.) is indeed the best practice in RESTful API design.

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    @Autowired
    private QuizService quizService;

    // Get all quizzes
    @GetMapping
    public List<Quiz> getAllQuizzes() {
        return quizService.getAllQuizzes();
    }

    // Get a quiz by ID
    @GetMapping("/{id}")
    public Optional<Quiz> getQuizById(@PathVariable Long id) {
        return quizService.getQuizById(id);
    }

    // Create a new quiz
    @PostMapping
    public Quiz createQuiz(@RequestBody Quiz quiz) {
        return quizService.createQuiz(quiz);
    }

    // Update an existing quiz
    @PutMapping("/{id}")
    public Quiz updateQuiz(@PathVariable Long id, @RequestBody Quiz quizDetails) {
        return quizService.updateQuiz(id, quizDetails);
    }

    // Delete a quiz
    @DeleteMapping("/{id}")
    public void deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
    }
}
