package com.example.quiz.repository;

import com.example.quiz.model.entity.GameEvent;
import com.example.quiz.model.entity.ModifierEffectsGameEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameEventRepository extends JpaRepository<GameEvent, Long> {

}
