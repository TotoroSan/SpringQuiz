package com.example.quiz.repository;

import com.example.quiz.model.entity.GameEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameEventRepository extends JpaRepository<GameEvent, Long> {

}
