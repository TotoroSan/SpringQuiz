package com.example.quiz.model.entity;

import org.springframework.stereotype.Component;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("HIGH_DIFFICULTY")
public class HighDifficultyQuizModifierEffect extends QuizModifierEffect {
    public HighDifficultyQuizModifierEffect() {
        super("HIGH_DIFFICULTY", "High Difficulty", 3);
    }
    
    public HighDifficultyQuizModifierEffect(int duration) {
        super("HIGH_DIFFICULTY", "High Difficulty", duration);
    }

    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setDifficultyModifier(quizModifier.getDifficultyModifier() + 5);
    }

	@Override
	public void reverse(QuizModifier quizModifier) {
		quizModifier.setDifficultyModifier(quizModifier.getDifficultyModifier() - 5);		
	}
}