package com.example.quiz.controller.admin;


import com.example.quiz.model.entity.Quiz;
import com.example.quiz.service.admin.AdminQuizService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// having the same URI for different actions (like create, update, and delete) but distinguishing them by the HTTP method (POST, PUT, DELETE, etc.) is indeed the best practice in RESTful API design.

@RestController
@RequestMapping("admin/api/quizzes")
public class AdminQuizController {

    @Autowired
    private AdminQuizService adminQuizService;

    // Get all quizzes
    @GetMapping
    public List<Quiz> getAllQuizzes() {
        return adminQuizService.getAllQuizzes();
    }

    // Get a quiz by ID
    @GetMapping("/{id}")
    public Optional<Quiz> getQuizById(@PathVariable Long id) {
        return adminQuizService.getQuizById(id);
    }

    // Create a new quiz
    @PostMapping
    public Quiz createQuiz(@RequestBody Quiz quiz) {
        return adminQuizService.createQuiz(quiz);
    }

    // Update an existing quiz
    @PutMapping("/{id}")
    public Quiz updateQuiz(@PathVariable Long id, @RequestBody Quiz quizDetails) {
        return adminQuizService.updateQuiz(id, quizDetails);
    }

    // Delete a quiz
    @DeleteMapping("/{id}")
    public void deleteQuiz(@PathVariable Long id) {
        adminQuizService.deleteQuiz(id);
    }
}
