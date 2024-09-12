package com.example.quiz.controller;

import com.example.quiz.model.QuizSubmission;
import com.example.quiz.service.QuizSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
public class QuizSubmissionController {

    @Autowired
    private QuizSubmissionService quizSubmissionService;

    // Submit a quiz
    @PostMapping
    public QuizSubmission submitQuiz(@RequestBody QuizSubmission submission) {
        return quizSubmissionService.submitQuiz(submission);
    }

    // Get all submissions by user ID
    @GetMapping("/user/{userId}")
    public List<QuizSubmission> getSubmissionsByUserId(@PathVariable Long userId) {
        return quizSubmissionService.getSubmissionsByUserId(userId);
    }

    // Get a specific submission by its ID
    @GetMapping("/{id}")
    public QuizSubmission getSubmissionById(@PathVariable Long id) {
        return quizSubmissionService.getSubmissionById(id);
    }

    // Update a quiz submission
    @PutMapping("/{id}")
    public QuizSubmission updateSubmission(@PathVariable Long id, @RequestBody QuizSubmission submissionDetails) {
        return quizSubmissionService.updateSubmission(id, submissionDetails);
    }

    // Delete a submission
    @DeleteMapping("/{id}")
    public void deleteSubmission(@PathVariable Long id) {
        quizSubmissionService.deleteSubmission(id);
    }
}
