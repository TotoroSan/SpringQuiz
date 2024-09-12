package com.example.quiz.repository;

import com.example.quiz.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    // Custom method to find quizzes by title
    List<Quiz> findByTitleContaining(String title);
}