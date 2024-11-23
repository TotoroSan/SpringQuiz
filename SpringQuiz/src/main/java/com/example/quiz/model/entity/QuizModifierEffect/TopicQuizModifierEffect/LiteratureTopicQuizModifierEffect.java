package com.example.quiz.model.entity.QuizModifierEffect.TopicQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CHOOSE_TOPIC_LITERATURE")
public class LiteratureTopicQuizModifierEffect extends QuizModifierEffect {
    protected LiteratureTopicQuizModifierEffect() {

    }

    public LiteratureTopicQuizModifierEffect(QuizModifier quizModifier) {
        super("CHOOSE_TOPIC_LITERATURE", "Choose Topic Literature", 3, quizModifier);
    }
    public LiteratureTopicQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("CHOOSE_TOPIC_LITERATURE", "Choose Topic Literature", duration, quizModifier);
    }


    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setTopicModifier("Literature");
    }

    @Override
    public void reverse(QuizModifier quizModifier) {
        quizModifier.setTopicModifier(null);
    }

}
