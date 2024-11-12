package com.example.quiz.model.entity;

import org.springframework.stereotype.Component;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;


@Entity
@DiscriminatorValue("QUADRUPLE_SCORE")
public class QuadrupleScoreQuizModifierEffect extends QuizModifierEffect {
    public QuadrupleScoreQuizModifierEffect() {
        super("QUADRUPLE_SCORE", "Quadruple Score", 3);
    }
    
    public QuadrupleScoreQuizModifierEffect(int duration) {
        super("QUADRUPLE_SCORE", "Quadruple Score", duration);
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

