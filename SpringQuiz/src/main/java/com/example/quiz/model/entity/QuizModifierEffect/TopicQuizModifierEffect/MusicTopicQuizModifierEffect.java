package com.example.quiz.model.entity.QuizModifierEffect.TopicQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CHOOSE_TOPIC_MUSIC")
public class MusicTopicQuizModifierEffect extends QuizModifierEffect {
    protected MusicTopicQuizModifierEffect() {

    }

    public MusicTopicQuizModifierEffect(QuizModifier quizModifier) {
        super("CHOOSE_TOPIC_MUSIC", "Choose Topic Music", 3, quizModifier);
    }
    public MusicTopicQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("CHOOSE_TOPIC_MUSIC", "Choose Topic Music", duration, quizModifier);
    }


    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setTopicModifier("Music");
    }

    @Override
    public void reverse(QuizModifier quizModifier) {
        quizModifier.setTopicModifier(null);
    }

}
