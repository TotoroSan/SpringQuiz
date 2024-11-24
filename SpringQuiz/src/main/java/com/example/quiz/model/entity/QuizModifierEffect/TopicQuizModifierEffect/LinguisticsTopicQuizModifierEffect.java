package com.example.quiz.model.entity.QuizModifierEffect.TopicQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CHOOSE_TOPIC_LINGUISTICS")
public class LinguisticsTopicQuizModifierEffect extends QuizModifierEffect {
    protected LinguisticsTopicQuizModifierEffect() {

    }

    public LinguisticsTopicQuizModifierEffect(QuizModifier quizModifier) {
        super("CHOOSE_TOPIC_LINGUISTICS", "Choose Topic Linguistics", 3, quizModifier, "Allows you to choose Linguistics as the topic for the next questions.", "topic", false, 1);
    }
    public LinguisticsTopicQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("CHOOSE_TOPIC_LINGUISTICS", "Choose Topic Linguistics", duration, quizModifier, "Allows you to choose Linguistics as the topic for the next questions.", "topic", false, 1);
    }


    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setTopicModifier("Linguistics");
    }

    @Override
    public void reverse(QuizModifier quizModifier) {
        quizModifier.setTopicModifier(null);
    }

}
