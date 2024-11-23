package com.example.quiz.model.entity.QuizModifierEffect.TopicQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CHOOSE_TOPIC_GEOGRAPHY")
public class GeographyTopicQuizModifierEffect extends QuizModifierEffect {
    protected GeographyTopicQuizModifierEffect() {

    }

    public GeographyTopicQuizModifierEffect(QuizModifier quizModifier) {
        super("CHOOSE_TOPIC_GEOGRAPHY", "Choose Topic Geography", 3, quizModifier);
    }
    public GeographyTopicQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("CHOOSE_TOPIC_GEOGRAPHY", "Choose Topic Geography", duration, quizModifier);
    }


    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setTopicModifier("Geography");
    }

    @Override
    public void reverse(QuizModifier quizModifier) {
        quizModifier.setTopicModifier(null);
    }

}
