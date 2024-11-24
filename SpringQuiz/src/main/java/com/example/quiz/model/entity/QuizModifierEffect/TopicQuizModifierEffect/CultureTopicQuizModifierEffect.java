package com.example.quiz.model.entity.QuizModifierEffect.TopicQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CHOOSE_TOPIC_CULTURE")
public class CultureTopicQuizModifierEffect extends QuizModifierEffect {
    protected CultureTopicQuizModifierEffect() {

    }

    public CultureTopicQuizModifierEffect(QuizModifier quizModifier) {
        super("CHOOSE_TOPIC_CULTURE", "Choose Topic Culture", 3, quizModifier,  "Allows you to choose Culture as the topic for the next questions.", "topic", false, 1);
    }
    public CultureTopicQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("CHOOSE_TOPIC_CULTURE", "Choose Topic Culture", duration, quizModifier,  "Allows you to choose Culture as the topic for the next questions.", "topic", false, 1);
    }


    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setTopicModifier("Culture");
    }

    @Override
    public void reverse(QuizModifier quizModifier) {
        quizModifier.setTopicModifier(null);
    }

}
