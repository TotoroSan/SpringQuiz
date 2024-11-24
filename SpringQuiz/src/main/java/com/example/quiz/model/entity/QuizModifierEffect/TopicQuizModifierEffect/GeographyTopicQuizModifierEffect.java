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
        super("CHOOSE_TOPIC_GEOGRAPHY", "Choose Topic Geography", 3, quizModifier, "Allows you to choose Geography as the topic for the next questions.", "topic", false, 1);
    }
    public GeographyTopicQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("CHOOSE_TOPIC_GEOGRAPHY", "Choose Topic Geography", duration, quizModifier, "Allows you to choose Geography as the topic for the next questions.", "topic", false, 1);
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
