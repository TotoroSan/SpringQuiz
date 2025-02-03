package com.example.quiz.model.entity.Joker;

import com.example.quiz.model.entity.QuizState;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TWENTYFIVE_SEVENTYFIVE")
public class TwentyFiveSeventyFiveJoker extends Joker {

    // Default constructor required by JPA
    protected TwentyFiveSeventyFiveJoker() {
        super();
    }

    /**
     * Constructs a TwentyFiveSeventyFiveJoker with the specified tier.
     * The number of uses is set to the default (1) multiplied by the tier.
     *
     * @param tier The joker tier.
     */
    public TwentyFiveSeventyFiveJoker(Integer tier) {
        // Example values: cost 120, rarity 2
        super("TWENTYFIVE_SEVENTYFIVE", "25/75", "Eliminates one wrong answer.", 120, (tier != null ? tier : 1), 2, tier);
    }


}
