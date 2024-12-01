package com.example.quiz.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

// currently used to store the last game event (can be expanded to history etc.)
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type")
public abstract class GameEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_state_id") // This creates the foreign key column
    private QuizState quizState;

    @Column(nullable = false)
    private LocalDateTime eventTimestamp;

    private boolean isConsumed;

    // Constructors
    public GameEvent() {
    }

    public GameEvent(QuizState quizState) {
        this.quizState = quizState;
        this.eventTimestamp = LocalDateTime.now();
        this.isConsumed = false;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public QuizState getQuizState() {
        return quizState;
    }

    public void setQuizState(QuizState quizState) {
        this.quizState = quizState;
    }

    public LocalDateTime getEventTimestamp() {
        return eventTimestamp;
    }

    public boolean isConsumed() {
        return isConsumed;
    }

    public void setConsumed(boolean b) {
        isConsumed = true;
    }
}


