package com.example.quiz.model.entity.QuizModifierEffect.TopicQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CHOOSE_TOPIC_ASTRONOMY")
public class AstronomyTopicQuizModifierEffect extends QuizModifierEffect {
    protected AstronomyTopicQuizModifierEffect() {

    }

    public AstronomyTopicQuizModifierEffect(QuizModifier quizModifier) {
        super("CHOOSE_TOPIC_ASTRONOMY", "Choose Topic Astronomy", 3, quizModifier);
    }
    public AstronomyTopicQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("CHOOSE_TOPIC_ASTRONOMY", "Choose Topic Astronomy", duration, quizModifier);
    }


    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setTopicModifier("Astronomy");
    }

    @Override
    public void reverse(QuizModifier quizModifier) {
        quizModifier.setTopicModifier(null);
    }

}
