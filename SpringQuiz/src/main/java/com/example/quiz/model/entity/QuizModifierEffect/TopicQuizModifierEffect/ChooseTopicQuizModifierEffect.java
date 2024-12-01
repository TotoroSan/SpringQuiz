package com.example.quiz.model.entity.QuizModifierEffect.TopicQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

// todo currently not in use - would be an effect that lets the user from a list of topics
@Entity
@DiscriminatorValue("CHOOSE_TOPIC")
public class ChooseTopicQuizModifierEffect extends QuizModifierEffect {
    private String topic = null;

    protected ChooseTopicQuizModifierEffect() {

    }

    // todo check what i need to keep here or whats depreceated
    public ChooseTopicQuizModifierEffect(QuizModifier quizModifier) {
        super("CHOOSE_TOPIC", "Choose Topic", 3, quizModifier, "Allows you to choose a topic for the next questions.", "topic", false, 1, null);
    }
    public ChooseTopicQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("CHOOSE_TOPIC", "Choose Topic", duration, quizModifier, "Allows you to choose a topic for the next questions.", "topic", false, 1, null);
    }
    public ChooseTopicQuizModifierEffect(QuizModifier quizModifier, int duration, String topic) {
        super("CHOOSE_TOPIC", "Choose Topic", duration, quizModifier, "Allows you to choose topic: " + topic + " for the next questions.", "topic", false, 1, null);
        this.topic = topic; // set topic
    }

    public ChooseTopicQuizModifierEffect(QuizModifier quizModifier, int duration, String topic, Integer tier) {
        // todo change what tier does here => currently it just extends the duration. we still keep duration as parameter for consistency.
        super("CHOOSE_TOPIC", "Choose Topic", 2 + (tier - 1), quizModifier, "Allows you to choose topic: " + topic + " for the next questions.", "topic", false, 1, tier);
        this.topic = topic; // set topic
    }


    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setTopicModifier(topic);
    }

    @Override
    public void reverse(QuizModifier quizModifier) {
        quizModifier.setTopicModifier(null);
    }

}
