package com.example.quiz.repository;

import com.example.quiz.model.entity.QuizState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizStateRepository extends JpaRepository<QuizState, Long> {
    // Custom query to find latest quiz states by userId
	Optional<QuizState> findFirstByUserIdOrderByIdDesc(Long userId);

    // Query is build by QUery derivation via keywords in method name
    Optional<QuizState> findFirstByUserIdAndIsActiveIsTrueOrderByIdDesc(Long userId);

    // Find all active QuizStates for a specific user
    List<QuizState> findAllByUserIdAndIsActiveTrue(Long userId);

    // Custom query to find all quiz states by userId
    Optional<QuizState> findByUserId(Long userId);

    List<QuizState> findAllByUserIdAndIsActiveFalse(Long userId);
}