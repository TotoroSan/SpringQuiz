package com.example.quiz.model.entity.QuizModifierEffect.TopicQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CHOOSE_TOPIC_CHEMISTRY")
public class ChemistryTopicQuizModifierEffect extends QuizModifierEffect {
    protected ChemistryTopicQuizModifierEffect() {

    }

    public ChemistryTopicQuizModifierEffect(QuizModifier quizModifier) {
        super("CHOOSE_TOPIC_CHEMISTRY", "Choose Topic Chemistry", 3, quizModifier, "Allows you to choose Chemistry as the topic for the next questions.", "topic", false, 1);
    }
    public ChemistryTopicQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("CHOOSE_TOPIC_CHEMISTRY", "Choose Topic Chemistry", duration, quizModifier, "Allows you to choose Chemistry as the topic for the next questions.", "topic", false, 1);
    }


    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setTopicModifier("Chemistry");
    }

    @Override
    public void reverse(QuizModifier quizModifier) {
        quizModifier.setTopicModifier(null);
    }

}
