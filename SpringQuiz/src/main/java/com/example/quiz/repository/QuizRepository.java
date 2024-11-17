package com.example.quiz.repository;

import com.example.quiz.model.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
	// TODO quiz repository 
}
