package com.example.quiz.model.entity.QuizModifierEffect.DifficultyQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MIN_DIFFICULTY_LIMIT")
public class MinDifficultyLimitQuizModifierEffect extends QuizModifierEffect {
    // Default constructor (required by JPA)
    protected MinDifficultyLimitQuizModifierEffect() {
    }

    public MinDifficultyLimitQuizModifierEffect(QuizModifier quizModifier) {
        super("MIN_DIFFICULTY_LIMIT", "Min Difficulty Limit", 3, quizModifier, "The next questions will have a given min difficulty.", "topic", false, 1, null);
    }

    public MinDifficultyLimitQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("MIN_DIFFICULTY_LIMIT", "Min Difficulty Limit", duration, quizModifier, "The next questions will have a given min difficulty.", "topic", false, 1, null);
    }

    public MinDifficultyLimitQuizModifierEffect(QuizModifier quizModifier, int duration, Integer tier) {
        super("MIN_DIFFICULTY_LIMIT", "Min Difficulty Limit", duration, quizModifier, "The next questions will have a given min difficulty.", "topic", false, 1, tier);
    }

    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setMinDifficultyModifier(this.getTier()); // todo edit arbitrary value if needed
    }

	@Override
	public void reverse(QuizModifier quizModifier) {
		quizModifier.setMinDifficultyModifier(null);
	}
}