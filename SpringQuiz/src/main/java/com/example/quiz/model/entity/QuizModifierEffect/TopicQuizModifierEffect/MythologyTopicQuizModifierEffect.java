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
        super("CHOOSE_TOPIC_MYTHOLOGY", "Choose Topic Mythology", 3, quizModifier);
    }
    public MythologyTopicQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("CHOOSE_TOPIC_MYTHOLOGY", "Choose Topic Mythology", duration, quizModifier);
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
