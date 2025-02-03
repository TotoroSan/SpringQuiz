package com.example.quiz.model.entity.Joker;

import com.example.quiz.model.entity.QuizState;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SKIP_QUESTION")
public class SkipQuestionJoker extends Joker {

    // Default constructor required by JPA
    protected SkipQuestionJoker() {
        super();
    }

    /**
     * Constructs a SkipQuestionJoker with the specified tier.
     * The number of uses is set to the default (1) multiplied by the tier.
     *
     * @param tier The joker tier.
     */
    public SkipQuestionJoker(Integer tier) {
        // Example values: cost 100, rarity 2
        super("SKIP_QUESTION", "Skip Question", "Allows you to skip the current question.", 100, (tier != null ? tier : 1), 2, tier);
    }

}
