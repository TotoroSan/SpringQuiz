package com.example.quiz.repository;

import com.example.quiz.model.entity.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {
    // Custom method to find all submissions by a specific user => spring automatically converts this into query. 
    //It recognises findAnyObjectAttribute and generate the according query. We do not need to implement the function explicitely.
    List<QuizSubmission> findByUserId(Long userId);

    // You can also add methods for finding submissions by quiz, etc.
}
