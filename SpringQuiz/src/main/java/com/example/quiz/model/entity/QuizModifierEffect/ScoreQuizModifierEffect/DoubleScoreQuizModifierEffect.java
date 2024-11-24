package com.example.quiz.model.entity.QuizModifierEffect.ScoreQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;


@Entity
@DiscriminatorValue("DOUBLE_SCORE")
public class DoubleScoreQuizModifierEffect extends QuizModifierEffect {
    // Default constructor (required by JPA)
    protected DoubleScoreQuizModifierEffect() {
    }

    public DoubleScoreQuizModifierEffect(QuizModifier quizModifier) {
        super("DOUBLE_SCORE", "Double Score", 3, quizModifier, "The next questions give double score.", "topic", false, 1);
    }
    
    public DoubleScoreQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("DOUBLE_SCORE", "Double Score", duration, quizModifier, "The next questions give double score.", "topic", false, 1);
    }
    

    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setScoreMultiplier(quizModifier.getScoreMultiplier() * 2);
    }
    
    @Override
    public void reverse(QuizModifier quizModifier) {
        quizModifier.setScoreMultiplier(quizModifier.getScoreMultiplier() / 2);
    }
}

