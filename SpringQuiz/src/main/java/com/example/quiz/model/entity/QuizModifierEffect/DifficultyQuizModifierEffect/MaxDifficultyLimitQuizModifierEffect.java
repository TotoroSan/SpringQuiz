package com.example.quiz.model.entity.QuizModifierEffect.DifficultyQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MAX_DIFFICULTY_LIMIT")
public class MaxDifficultyLimitQuizModifierEffect extends QuizModifierEffect {
    // Default constructor (required by JPA)
    protected MaxDifficultyLimitQuizModifierEffect() {
    }

    public MaxDifficultyLimitQuizModifierEffect(QuizModifier quizModifier) {
        super("MAX_DIFFICULTY_LIMIT", "Max Difficulty Limit", 3, quizModifier, "The next questions will have a given max difficulty.", "topic", false, 1);
    }

    public MaxDifficultyLimitQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("MAX_DIFFICULTY_LIMIT", "Max Difficulty Limit", duration, quizModifier, "The next questions will have a given max difficulty.", "topic", false, 1);
    }

    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setMaxDifficultyModifier(3); // todo 3 is an arbitrary value for testing, ideally we would set this dynamically
    }

	@Override
	public void reverse(QuizModifier quizModifier) {
		quizModifier.setMaxDifficultyModifier(null);
	}
}