package com.example.quiz.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

// currently used to store the last game event (can be expanded to history etc.)
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type")
public class GameEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private QuizState quizState;

    @Column(nullable = false)
    private LocalDateTime eventTimestamp;

    // Constructors
    public GameEvent() {
        this.eventTimestamp = LocalDateTime.now();
    }

    public GameEvent(QuizState quizState) {
        this.quizState = quizState;
        this.eventTimestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public QuizState getQuizState() {
        return quizState;
    }

    public LocalDateTime getEventTimestamp() {
        return eventTimestamp;
    }

    public void setQuizState(QuizState quizState) {
        this.quizState = quizState;
    }
}


