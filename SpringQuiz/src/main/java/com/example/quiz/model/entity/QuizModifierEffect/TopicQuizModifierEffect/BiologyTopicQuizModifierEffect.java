package com.example.quiz.model.entity.QuizModifierEffect.TopicQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CHOOSE_TOPIC_BIOLOGY")
public class BiologyTopicQuizModifierEffect extends QuizModifierEffect {
    protected BiologyTopicQuizModifierEffect() {

    }

    public BiologyTopicQuizModifierEffect(QuizModifier quizModifier) {
        super("CHOOSE_TOPIC_BIOLOGY", "Choose Topic Biology", 3, quizModifier, "Allows you to choose Biology as the topic for the next questions.", "topic", false, 1);
    }
    public BiologyTopicQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("CHOOSE_TOPIC_BIOLOGY", "Choose Topic Biology", duration, quizModifier, "Allows you to choose Biology as the topic for the next questions.", "topic", false, 1);
    }


    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setTopicModifier("Biology");
    }

    @Override
    public void reverse(QuizModifier quizModifier) {
        quizModifier.setTopicModifier(null);
    }

}
