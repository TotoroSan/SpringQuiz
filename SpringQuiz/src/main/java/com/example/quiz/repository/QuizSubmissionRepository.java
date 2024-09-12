package com.example.quiz.repository;

import com.example.quiz.model.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {
    // Custom method to find all submissions by a specific user
    List<QuizSubmission> findByUserId(Long userId);

    // You can also add methods for finding submissions by quiz, etc.
}
