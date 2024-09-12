package com.example.quiz.repository;

import com.example.quiz.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    // You can add custom methods here for specific queries if needed
}
