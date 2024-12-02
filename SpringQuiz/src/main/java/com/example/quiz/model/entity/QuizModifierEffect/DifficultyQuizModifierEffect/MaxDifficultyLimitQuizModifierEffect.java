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
        super("MAX_DIFFICULTY_LIMIT", "Max Difficulty Limit", 3, quizModifier, "The next questions will have a given max difficulty.", "topic", false, 1, null);
    }

    public MaxDifficultyLimitQuizModifierEffect(QuizModifier quizModifier, Integer duration) {
        super("MAX_DIFFICULTY_LIMIT", "Max Difficulty Limit", duration, quizModifier, "The next questions will have a given max difficulty.", "topic", false, 1, null);
    }

    public MaxDifficultyLimitQuizModifierEffect(QuizModifier quizModifier, Integer duration, Integer tier) {
        super("MAX_DIFFICULTY_LIMIT", "Max Difficulty Limit", duration, quizModifier, "The next questions will have a given max difficulty.", "topic", false, 1, tier);
    }

    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setMaxDifficultyModifier(6 - this.getTier()); // inverted scaling with tier. higher tier -> lower max difficulty.
    }

    @Override
    public void reverse(QuizModifier quizModifier) {
        quizModifier.setMaxDifficultyModifier(null);
    }
}