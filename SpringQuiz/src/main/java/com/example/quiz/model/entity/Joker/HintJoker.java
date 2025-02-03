package com.example.quiz.model.entity.Joker;

import com.example.quiz.model.entity.QuizState;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("HINT")
public class HintJoker extends Joker {

    // Default constructor required by JPA
    protected HintJoker() {
        super();
    }

    /**
     * Constructs a HintJoker with the specified tier.
     * The number of uses is set to the default (1) multiplied by the tier.
     *
     * @param tier The joker tier.
     */
    public HintJoker(Integer tier) {
        // Example values: cost 80, rarity 1
        super("HINT", "Hint", "Provides a hint for the current question.", 80, (tier != null ? tier : 1), 1, tier);
    }


}
