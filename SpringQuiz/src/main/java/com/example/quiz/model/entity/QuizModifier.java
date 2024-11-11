package com.example.quiz.model.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;

@Entity
public class QuizModifier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double scoreMultiplier = 1.0;
    private int difficultyModifier = 0;
    
    // TODO here is the problem currently -
    // we only persist the effect id
    @OneToMany(mappedBy = "quizModifier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizModifierEffect> activeQuizModifierEffects = new ArrayList<>();
    
    @OneToOne
    private QuizState quizState;
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public QuizState getQuizState() {
		return quizState;
	}

	public void setQuizState(QuizState quizState) {
		this.quizState = quizState;
	}

	// Getters and setters for scoreMultiplier, difficultyModifier, and activeModifiers
    public double getScoreMultiplier() {
        return scoreMultiplier;
    }

    public void setScoreMultiplier(double scoreMultiplier) {
        this.scoreMultiplier = scoreMultiplier;
    }

    public int getDifficultyModifier() {
        return difficultyModifier;
    }

    public void setDifficultyModifier(int difficultyModifier) {
        this.difficultyModifier = difficultyModifier;
    }

    public List<QuizModifierEffect> getActiveQuizModifierEffects() {
        return activeQuizModifierEffects;
    }
    public void setActiveQuizModifierEffects(List<QuizModifierEffect> activeQuizModifierEffects) {
        this.activeQuizModifierEffects = activeQuizModifierEffects;
    }
}