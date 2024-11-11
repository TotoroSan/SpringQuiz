package com.example.quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect;

public interface QuizModifierRepository extends JpaRepository<QuizModifier, Long> {
	// TODO quiz repository 
}
