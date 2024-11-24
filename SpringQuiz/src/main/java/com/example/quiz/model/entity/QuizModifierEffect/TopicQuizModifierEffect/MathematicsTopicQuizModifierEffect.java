package com.example.quiz.model.entity.QuizModifierEffect.TopicQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CHOOSE_TOPIC_MATHEMATICS")
public class MathematicsTopicQuizModifierEffect extends QuizModifierEffect {
    protected MathematicsTopicQuizModifierEffect() {

    }

    public MathematicsTopicQuizModifierEffect(QuizModifier quizModifier) {
        super("CHOOSE_TOPIC_MATHEMATICS", "Choose Topic Mathematics", 3, quizModifier, "Allows you to choose Mathematics as the topic for the next questions.", "topic", false, 1);
    }
    public MathematicsTopicQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("CHOOSE_TOPIC_MATHEMATICS", "Choose Topic Mathematics", duration, quizModifier, "Allows you to choose Mathematics as the topic for the next questions.", "topic", false, 1);
    }


    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setTopicModifier("Mathematics");
    }

    @Override
    public void reverse(QuizModifier quizModifier) {
        quizModifier.setTopicModifier(null);
    }

}
