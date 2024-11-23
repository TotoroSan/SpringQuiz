package com.example.quiz.model.entity.QuizModifierEffect.TopicQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CHOOSE_TOPIC_PHYSICS")
public class PhysicsTopicQuizModifierEffect extends QuizModifierEffect {
    protected PhysicsTopicQuizModifierEffect() {

    }

    public PhysicsTopicQuizModifierEffect(QuizModifier quizModifier) {
        super("CHOOSE_TOPIC_PHYSICS", "Choose Topic Phsyics", 3, quizModifier);
    }
    public PhysicsTopicQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("CHOOSE_TOPIC_PHYSICS", "Choose Topic Phsyics", duration, quizModifier);
    }


    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setTopicModifier("Phsyics");
    }

    @Override
    public void reverse(QuizModifier quizModifier) {
        quizModifier.setTopicModifier(null);
    }

}
