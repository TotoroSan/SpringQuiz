package com.example.quiz.model.entity.QuizModifierEffect.TopicQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CHOOSE_TOPIC_SCIENCE")
public class ScienceTopicQuizModifierEffect extends QuizModifierEffect {
    protected ScienceTopicQuizModifierEffect() {

    }

    public ScienceTopicQuizModifierEffect(QuizModifier quizModifier) {
        super("CHOOSE_TOPIC_SCIENCE", "Choose Topic Science", 3, quizModifier);
    }
    public ScienceTopicQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("CHOOSE_TOPIC_SCIENCE", "Choose Topic Science", duration, quizModifier);
    }


    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setTopicModifier("Science");
    }

    @Override
    public void reverse(QuizModifier quizModifier) {
        quizModifier.setTopicModifier(null);
    }

}
