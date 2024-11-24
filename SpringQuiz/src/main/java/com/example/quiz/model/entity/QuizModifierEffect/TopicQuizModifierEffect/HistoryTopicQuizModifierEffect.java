package com.example.quiz.model.entity.QuizModifierEffect.TopicQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CHOOSE_TOPIC_HISTORY")
public class HistoryTopicQuizModifierEffect extends QuizModifierEffect {
    protected HistoryTopicQuizModifierEffect() {

    }

    public HistoryTopicQuizModifierEffect(QuizModifier quizModifier) {
        super("CHOOSE_TOPIC_HISTORY", "Choose Topic History", 3, quizModifier, "Allows you to choose History as the topic for the next questions.", "topic", false, 1);
    }
    public HistoryTopicQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("CHOOSE_TOPIC_HISTORY", "Choose Topic History", duration, quizModifier, "Allows you to choose History as the topic for the next questions.", "topic", false, 1);
    }


    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setTopicModifier("History");
    }

    @Override
    public void reverse(QuizModifier quizModifier) {
        quizModifier.setTopicModifier(null);
    }

}
