package com.example.quiz.model.entity.QuizModifierEffect.TopicQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CHOOSE_TOPIC_GEOLOGY")
public class GeologyTopicQuizModifierEffect extends QuizModifierEffect {
    protected GeologyTopicQuizModifierEffect() {

    }

    public GeologyTopicQuizModifierEffect(QuizModifier quizModifier) {
        super("CHOOSE_TOPIC_GEOLOGY", "Choose Topic Geology", 3, quizModifier);
    }
    public GeologyTopicQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("CHOOSE_TOPIC_GEOLOGY", "Choose Topic Geology", duration, quizModifier);
    }


    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setTopicModifier("Geology");
    }

    @Override
    public void reverse(QuizModifier quizModifier) {
        quizModifier.setTopicModifier(null);
    }

}
