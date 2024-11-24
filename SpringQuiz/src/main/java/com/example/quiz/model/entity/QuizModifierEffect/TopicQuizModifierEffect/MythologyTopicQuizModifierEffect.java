package com.example.quiz.model.entity.QuizModifierEffect.TopicQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CHOOSE_TOPIC_MYTHOLOGY")
public class MythologyTopicQuizModifierEffect extends QuizModifierEffect {
    protected MythologyTopicQuizModifierEffect() {

    }

    public MythologyTopicQuizModifierEffect(QuizModifier quizModifier) {
        super("CHOOSE_TOPIC_MYTHOLOGY", "Choose Topic Mythology", 3, quizModifier, "Allows you to choose Mythology as the topic for the next questions.", "topic", false, 1);
    }
    public MythologyTopicQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("CHOOSE_TOPIC_MYTHOLOGY", "Choose Topic Mythology", duration, quizModifier, "Allows you to choose Mythology as the topic for the next questions.", "topic", false, 1);
    }


    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setTopicModifier("Mythology");
    }

    @Override
    public void reverse(QuizModifier quizModifier) {
        quizModifier.setTopicModifier(null);
    }

}
