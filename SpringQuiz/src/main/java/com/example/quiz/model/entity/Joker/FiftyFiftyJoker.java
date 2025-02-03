package com.example.quiz.model.entity.Joker;

import com.example.quiz.model.entity.QuizState;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FIFTY_FIFTY")
public class FiftyFiftyJoker extends Joker {

    // Default constructor required by JPA
    protected FiftyFiftyJoker() {
        super();
    }

    /**
     * Constructs a FiftyFiftyJoker with the specified tier.
     * The number of uses is set to the default (1) multiplied by the tier.
     *
     * @param tier The joker tier.
     */
    public FiftyFiftyJoker(Integer tier) {
        // Example values: cost 150, rarity 3
        super("FIFTY_FIFTY", "50/50", "Eliminates two wrong answers.", 150, (tier != null ? tier : 1), 3, tier);
    }

}
