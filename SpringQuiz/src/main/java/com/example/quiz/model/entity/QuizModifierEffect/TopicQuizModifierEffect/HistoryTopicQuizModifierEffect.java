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
        super("CHOOSE_TOPIC_HISTORY", "Choose Topic History", 3, quizModifier);
    }
    public HistoryTopicQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("CHOOSE_TOPIC_HISTORY", "Choose Topic History", duration, quizModifier);
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
