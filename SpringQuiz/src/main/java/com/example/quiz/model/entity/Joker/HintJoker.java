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

    public HintJoker(String idString, String name, String description, Integer cost, Integer uses, Integer rarity, Integer tier) {
        super(idString, name, description, cost, uses, rarity, tier);
    }

}
