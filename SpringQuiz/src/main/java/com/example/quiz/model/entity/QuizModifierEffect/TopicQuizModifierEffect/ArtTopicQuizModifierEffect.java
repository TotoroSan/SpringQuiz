package com.example.quiz.model.entity.QuizModifierEffect.TopicQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CHOOSE_TOPIC_ART")
public class ArtTopicQuizModifierEffect extends QuizModifierEffect {
    protected ArtTopicQuizModifierEffect() {

    }

    public ArtTopicQuizModifierEffect(QuizModifier quizModifier) {
        super("CHOOSE_TOPIC_ART", "Choose Topic Art", 3, quizModifier, "Allows you to choose Art as the topic for the next questions.", "topic", false, 1);
    }
    public ArtTopicQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("CHOOSE_TOPIC_ART", "Choose Topic Art", duration, quizModifier, "Allows you to choose Art as the topic for the next questions.", "topic", false, 1);
    }


    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setTopicModifier("Art");
    }

    @Override
    public void reverse(QuizModifier quizModifier) {
        quizModifier.setTopicModifier(null);
    }

}
