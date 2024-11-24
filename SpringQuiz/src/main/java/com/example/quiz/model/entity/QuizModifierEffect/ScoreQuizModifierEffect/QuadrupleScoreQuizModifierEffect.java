package com.example.quiz.model.entity.QuizModifierEffect.ScoreQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;


@Entity
@DiscriminatorValue("QUADRUPLE_SCORE")
public class QuadrupleScoreQuizModifierEffect extends QuizModifierEffect {
    // Default constructor (required by JPA)
    protected QuadrupleScoreQuizModifierEffect() {

    }

    public QuadrupleScoreQuizModifierEffect(QuizModifier quizModifier) {
        super("QUADRUPLE_SCORE", "Quadruple Score", 3, quizModifier, "The next questions give quadruple score.", "topic", false, 1);
    }
    
    public QuadrupleScoreQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("QUADRUPLE_SCORE", "Quadruple Score", duration, quizModifier, "The next questions give quadruple score.", "topic", false, 1);
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

