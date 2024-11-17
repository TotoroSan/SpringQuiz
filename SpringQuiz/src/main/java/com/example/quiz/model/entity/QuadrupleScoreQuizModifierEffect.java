package com.example.quiz.model.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;


@Entity
@DiscriminatorValue("QUADRUPLE_SCORE")
public class QuadrupleScoreQuizModifierEffect extends QuizModifierEffect {
    // Default constructor (required by JPA)
    protected QuadrupleScoreQuizModifierEffect() {

    }

    public QuadrupleScoreQuizModifierEffect(QuizModifier quizModifier) {
        super("QUADRUPLE_SCORE", "Quadruple Score", 3, quizModifier);
    }
    
    public QuadrupleScoreQuizModifierEffect(int duration, QuizModifier quizModifier) {
        super("QUADRUPLE_SCORE", "Quadruple Score", duration, quizModifier);
    }

    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setScoreMultiplier(quizModifier.getScoreMultiplier() * 4);
    }
    
    @Override
    public void reverse(QuizModifier quizModifier) {
        quizModifier.setScoreMultiplier(quizModifier.getScoreMultiplier() / 4);
    }
}

