package com.example.quiz.repository;


import com.example.quiz.model.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    // Add custom methods here if necessary
}
