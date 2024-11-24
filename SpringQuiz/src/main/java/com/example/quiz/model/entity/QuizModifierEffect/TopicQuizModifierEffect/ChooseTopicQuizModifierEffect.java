package com.example.quiz.model.entity.QuizModifierEffect.TopicQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

// todo currently not in use - would be an effect that lets the user from a list of topics
@Entity
@DiscriminatorValue("CHOOSE_TOPIC")
public class ChooseTopicQuizModifierEffect extends QuizModifierEffect {
    protected ChooseTopicQuizModifierEffect() {

    }

    public ChooseTopicQuizModifierEffect(QuizModifier quizModifier) {
        super("CHOOSE_TOPIC", "Choose Topic", 3, quizModifier, "Allows you to choose a topic for the next questions.", "topic", false, 1);
    }
    public ChooseTopicQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("CHOOSE_TOPIC", "Choose Topic", duration, quizModifier, "Allows you to choose a topic for the next questions.", "topic", false, 1);
    }


    @Override
    public void apply(QuizModifier quizModifier) {
        // todo
    }

    @Override
    public void reverse(QuizModifier quizModifier) {
        // todo
    }

}
