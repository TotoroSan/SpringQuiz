package com.example.quiz.model.entity.QuizModifierEffect.TopicQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CHOOSE_TOPIC_MEDICINE")
public class MedicineTopicQuizModifierEffect extends QuizModifierEffect {
    protected MedicineTopicQuizModifierEffect() {

    }

    public MedicineTopicQuizModifierEffect(QuizModifier quizModifier) {
        super("CHOOSE_TOPIC_MEDICINE", "Choose Topic Medicine", 3, quizModifier);
    }
    public MedicineTopicQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("CHOOSE_TOPIC_MEDICINE", "Choose Topic Medicine", duration, quizModifier);
    }


    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setTopicModifier("Medicine");
    }

    @Override
    public void reverse(QuizModifier quizModifier) {
        quizModifier.setTopicModifier(null);
    }

}
