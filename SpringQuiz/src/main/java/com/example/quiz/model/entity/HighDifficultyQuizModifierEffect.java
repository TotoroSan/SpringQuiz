package com.example.quiz.model.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("HIGH_DIFFICULTY")
public class HighDifficultyQuizModifierEffect extends QuizModifierEffect {
    // Default constructor (required by JPA)
    protected HighDifficultyQuizModifierEffect() {
    }

    // TODO instantiate this and other classes with the meta data? this way duration can be set in the EffectFactory.
    // TODO its best to hardcode the inforamtion only in one place. instead of static attributes we chose the registry approach.
    public HighDifficultyQuizModifierEffect(QuizModifier quizModifier) {
        super("HIGH_DIFFICULTY", "High Difficulty", 3, quizModifier);
    }
    
    public HighDifficultyQuizModifierEffect(int duration, QuizModifier quizModifier) {
        super("HIGH_DIFFICULTY", "High Difficulty", duration, quizModifier);
    }

    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setDifficultyModifier(quizModifier.getDifficultyModifier() + 1);
    }

	@Override
	public void reverse(QuizModifier quizModifier) {
		quizModifier.setDifficultyModifier(quizModifier.getDifficultyModifier() - 1);
	}
}