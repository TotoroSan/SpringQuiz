package com.example.quiz.model.entity.QuizModifierEffect.TopicQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CHOOSE_TOPIC_TECHNOLOGY")
public class TechnologyTopicQuizModifierEffect extends QuizModifierEffect {
    protected TechnologyTopicQuizModifierEffect() {

    }

    public TechnologyTopicQuizModifierEffect(QuizModifier quizModifier) {
        super("CHOOSE_TOPIC_TECHNOLOGY", "Choose Topic Technology", 3, quizModifier, "Allows you to choose Technology as the topic for the next questions.", "topic", false, 1);
    }
    public TechnologyTopicQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("CHOOSE_TOPIC_TECHNOLOGY", "Choose Topic Technology", duration, quizModifier,"Allows you to choose Technology as the topic for the next questions.", "topic", false, 1);
    }


    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setTopicModifier("Technology");
    }

    @Override
    public void reverse(QuizModifier quizModifier) {
        quizModifier.setTopicModifier(null);
    }

}
