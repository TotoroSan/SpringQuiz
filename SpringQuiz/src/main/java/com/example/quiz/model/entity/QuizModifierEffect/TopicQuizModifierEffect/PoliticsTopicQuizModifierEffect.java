package com.example.quiz.model.entity.QuizModifierEffect.TopicQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CHOOSE_TOPIC_POLITICS")
public class PoliticsTopicQuizModifierEffect extends QuizModifierEffect {
    protected PoliticsTopicQuizModifierEffect() {

    }

    public PoliticsTopicQuizModifierEffect(QuizModifier quizModifier) {
        super("CHOOSE_TOPIC_POLITICS", "Choose Topic Politics", 3, quizModifier,  "Allows you to choose Politics as the topic for the next questions.", "topic", false, 1);
    }
    public PoliticsTopicQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("CHOOSE_TOPIC_POLITICS", "Choose Topic Politics", duration, quizModifier,  "Allows you to choose Politics as the topic for the next questions.", "topic", false, 1);
    }


    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setTopicModifier("Politics");
    }

    @Override
    public void reverse(QuizModifier quizModifier) {
        quizModifier.setTopicModifier(null);
    }

}
