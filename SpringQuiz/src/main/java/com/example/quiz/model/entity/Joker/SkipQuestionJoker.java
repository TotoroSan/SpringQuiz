package com.example.quiz.model.entity.Joker;

import com.example.quiz.model.entity.QuizState;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SKIP_QUESTION")
public class SkipQuestionJoker extends Joker {

    protected SkipQuestionJoker() {
        super();
    }

    public SkipQuestionJoker(String idString, String name, String description, Integer cost, Integer uses, Integer rarity, Integer tier, QuizState quizState) {
        super(idString, name, description, cost, uses, rarity, tier, quizState);
    }
}
