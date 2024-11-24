package com.example.quiz.model.entity.QuizModifierEffect.TopicQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CHOOSE_TOPIC_SPORTS")
public class SportsTopicQuizModifierEffect extends QuizModifierEffect {
    protected SportsTopicQuizModifierEffect() {

    }

    public SportsTopicQuizModifierEffect(QuizModifier quizModifier) {
        super("CHOOSE_TOPIC_SPORTS", "Choose Topic Sports", 3, quizModifier,  "Allows you to choose Sports as the topic for the next questions.", "topic", false, 1);
    }
    public SportsTopicQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("CHOOSE_TOPIC_SPORTS", "Choose Topic Sports", duration, quizModifier,  "Allows you to choose Sports as the topic for the next questions.", "topic", false, 1);
    }


    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setTopicModifier("Sports");
    }

    @Override
    public void reverse(QuizModifier quizModifier) {
        quizModifier.setTopicModifier(null);
    }

}
