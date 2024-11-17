package com.example.quiz.controller.admin;

import com.example.quiz.model.entity.QuizSubmission;
import com.example.quiz.service.admin.AdminQuizSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/api/submissions")
public class AdminQuizSubmissionController {

    @Autowired
    private AdminQuizSubmissionService adminQuizSubmissionService;

    // Submit a quiz
    @PostMapping
    public QuizSubmission submitQuiz(@RequestBody QuizSubmission submission) {
        return adminQuizSubmissionService.submitQuiz(submission);
    }

    // Get all submissions by user ID
    @GetMapping("/user/{userId}")
    public List<QuizSubmission> getSubmissionsByUserId(@PathVariable Long userId) {
        return adminQuizSubmissionService.getSubmissionsByUserId(userId);
    }

    // Get a specific submission by its ID
    @GetMapping("/{id}")
    public QuizSubmission getSubmissionById(@PathVariable Long id) {
        return adminQuizSubmissionService.getSubmissionById(id);
    }

    // Update a quiz submission
    @PutMapping("/{id}")
    public QuizSubmission updateSubmission(@PathVariable Long id, @RequestBody QuizSubmission submissionDetails) {
        return adminQuizSubmissionService.updateSubmission(id, submissionDetails);
    }

    // Delete a submission
    @DeleteMapping("/{id}")
    public void deleteSubmission(@PathVariable Long id) {
        adminQuizSubmissionService.deleteSubmission(id);
    }
}
