package com.example.quiz.model.entity.QuizModifierEffect.TopicQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CHOOSE_TOPIC_ECONOMICS")
public class EconomicsTopicQuizModifierEffect extends QuizModifierEffect {
    protected EconomicsTopicQuizModifierEffect() {

    }

    public EconomicsTopicQuizModifierEffect(QuizModifier quizModifier) {
        super("CHOOSE_TOPIC_ECONOMICS", "Choose Topic Economics", 3, quizModifier);
    }
    public EconomicsTopicQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("CHOOSE_TOPIC_ECONOMICS", "Choose Topic Economics", duration, quizModifier);
    }


    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setTopicModifier("Economics");
    }

    @Override
    public void reverse(QuizModifier quizModifier) {
        quizModifier.setTopicModifier(null);
    }

}
