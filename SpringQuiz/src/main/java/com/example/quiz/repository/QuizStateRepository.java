package com.example.quiz.repository;

import com.example.quiz.model.entity.QuizState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizStateRepository extends JpaRepository<QuizState, Long> {
    // Custom query to find latest quiz states by userId
	Optional<QuizState> findFirstByUserIdOrderByIdDesc(Long userId);

    // Custom query to find all quiz states by userId
    Optional<QuizState> findByUserId(Long userId);
}