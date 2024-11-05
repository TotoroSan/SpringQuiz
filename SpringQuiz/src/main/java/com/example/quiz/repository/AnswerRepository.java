package com.example.quiz.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.quiz.model.entity.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    // Add custom methods here if necessary
}
