package com.example.quiz.service.admin;


import com.example.quiz.exception.ResourceNotFoundException;
import com.example.quiz.model.QuizSubmission;
import com.example.quiz.repository.QuizSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminQuizSubmissionService {

    @Autowired
    private QuizSubmissionRepository quizSubmissionRepository;

    // Submit a quiz (create a QuizSubmission)
    public QuizSubmission submitQuiz(QuizSubmission submission) {
        return quizSubmissionRepository.save(submission);
    }

    // Get all submissions by a user ID
    public List<QuizSubmission> getSubmissionsByUserId(Long userId) {
        return quizSubmissionRepository.findByUserId(userId);
    }

    // Get a specific submission by its ID
    public QuizSubmission getSubmissionById(Long id) {
        return quizSubmissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found for this id :: " + id));
    }

    // Update a submission
    public QuizSubmission updateSubmission(Long id, QuizSubmission submissionDetails) {
        QuizSubmission submission = quizSubmissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found for this id :: " + id));

        submission.setScore(submissionDetails.getScore());
        return quizSubmissionRepository.save(submission);
    }

    // Delete a submission
    public void deleteSubmission(Long id) {
        QuizSubmission submission = quizSubmissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found for this id :: " + id));

        quizSubmissionRepository.delete(submission);
    }
}
