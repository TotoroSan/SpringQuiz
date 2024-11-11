package com.example.quiz.model.entity;

import org.springframework.stereotype.Component;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;


@Entity
@DiscriminatorValue("DOUBLE_SCORE")
public class DoubleScoreQuizModifierEffect extends QuizModifierEffect {
    public DoubleScoreQuizModifierEffect() {
        super("DOUBLE_SCORE", "Double Score", 3);
    }
    
    public DoubleScoreQuizModifierEffect(int duration) {
        super("DOUBLE_SCORE", "Double Score", duration);
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

