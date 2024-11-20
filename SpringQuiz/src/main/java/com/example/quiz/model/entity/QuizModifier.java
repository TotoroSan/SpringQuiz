package com.example.quiz.model.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

// contains all modifiable quiz parameters
@Entity
public class QuizModifier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double scoreMultiplier = 1.0;
    private int difficultyModifier = 1;
    private String topicModifier = null; // null meaning all topics are possible // todo conver to a list for multiple topics possible

    // orphanRemoval = true because if a effect is no longer active (= expired), it can be deleted
    @OneToMany(mappedBy = "quizModifier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizModifierEffect> activeQuizModifierEffects = new ArrayList<>();

    @OneToOne
    private QuizState quizState;

    // Default Constructor
    QuizModifier(){}

    // Each QuizModifier belongs to one QuizsState (Jpa needs this to set both sides of OneToOne relationship in table)
    QuizModifier(QuizState quizState){
        this.quizState = quizState;
    }

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

    public String getTopicModifier() {
        return topicModifier;
    }

    public void setTopicModifier(String topicModifier) {
        this.topicModifier = topicModifier;
    }

    public List<QuizModifierEffect> getActiveQuizModifierEffects() {
        return activeQuizModifierEffects;
    }
    public void setActiveQuizModifierEffects(List<QuizModifierEffect> activeQuizModifierEffects) {
        this.activeQuizModifierEffects = activeQuizModifierEffects;
    }

    public void addActiveQuizModifierEffect(QuizModifierEffect quizModifierEffect){
        this.activeQuizModifierEffects.add(quizModifierEffect);
    }

}