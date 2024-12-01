package com.example.quiz.model.entity.QuizModifierEffect.LifeQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("INCREASE_LIFE_COUNTER")
public class IncreaseLifeCounterQuizModifierEffect extends QuizModifierEffect {
    // Default constructor (required by JPA)
    protected IncreaseLifeCounterQuizModifierEffect() {
    }

    public IncreaseLifeCounterQuizModifierEffect(QuizModifier quizModifier) {
        super("INCREASE_LIFE_COUNTER", "Increase Life Counter", null, quizModifier, "Increase your life count by 1.", "topic", true, 1, null);
    }

    public IncreaseLifeCounterQuizModifierEffect(QuizModifier quizModifier, Integer tier) {
        super("INCREASE_LIFE_COUNTER", "Increase Life Counter", null, quizModifier, "Increase your life count by 1.", "topic", true, 1, tier);
    }

    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setLifeCounter(quizModifier.getLifeCounter() + this.getTier());
    }

    @Override
    public void reverse(QuizModifier quizModifier) {
        quizModifier.setLifeCounter(quizModifier.getLifeCounter() - this.getTier());
    }
}
