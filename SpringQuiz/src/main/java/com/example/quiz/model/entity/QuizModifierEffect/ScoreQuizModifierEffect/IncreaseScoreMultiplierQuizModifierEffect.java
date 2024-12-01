package com.example.quiz.model.entity.QuizModifierEffect.ScoreQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

// todo generalized version of other modifier effects
@Entity
@DiscriminatorValue("INCREASE_SCORE_MULTIPLIER")
public class IncreaseScoreMultiplierQuizModifierEffect extends QuizModifierEffect {
    double factor; // the factor by which the multiplier is increased is tied to the tier

    // Default constructor (required by JPA)
    protected IncreaseScoreMultiplierQuizModifierEffect() {
    }

    public IncreaseScoreMultiplierQuizModifierEffect(QuizModifier quizModifier) {
        super("INCREASE_SCORE_MULTIPLIER", "Increase Score Multiplier", 3, quizModifier, "The next questions give x score.", "score", false, 1, null);
    }

    public IncreaseScoreMultiplierQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("INCREASE_SCORE_MULTIPLIER", "Increase Score Multiplier", duration, quizModifier, "The next questions give x score.", "score", false, 1, null);
    }

    public IncreaseScoreMultiplierQuizModifierEffect(QuizModifier quizModifier, int duration, Integer tier) {
        super("INCREASE_SCORE_MULTIPLIER", "Increase Score Multiplier (Tier " + tier + ")", duration, quizModifier, "The next questions give" + calculateMultiplierByTier(tier) + "x score.", "score", false, 1, tier);
    }

    private static int calculateMultiplierByTier(Integer tier) {
        int baseMultiplier = 2; // Base multiplier for Tier 1
        return baseMultiplier + (tier != null ? tier - 1 : 0); // Add 1 per tier above 1
    }

    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setScoreMultiplier(quizModifier.getScoreMultiplier() * calculateMultiplierByTier(this.getTier()));
    }

    @Override
    public void reverse(QuizModifier quizModifier) {
        quizModifier.setScoreMultiplier(quizModifier.getScoreMultiplier() / calculateMultiplierByTier(this.getTier()));
    }
}

