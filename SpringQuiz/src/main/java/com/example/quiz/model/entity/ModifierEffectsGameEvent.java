package com.example.quiz.model.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("MODIFIER_EFFECTS")
public class ModifierEffectsGameEvent extends GameEvent {

    // we only save the id strings and use them to create the presented effect dtos again
    // we need to do this because we do not instantiate / save the presented effects
    // could also save the modifier effects but thats not good because then i am mixing
    // active modifier effects and selection modifier effects in one table
    @ElementCollection
    private List<String> presentedEffectIdStrings; // Stores the IDs of the presented modifier effects

    // Constructors
    public ModifierEffectsGameEvent() {
        super();
    }

    public ModifierEffectsGameEvent(QuizState quizState, List<String> presentedEffectIdStrings) {
        super(quizState);
        this.presentedEffectIdStrings = presentedEffectIdStrings;
    }

    // Getters and Setters
    public List<String> getPresentedEffectIdStrings() {
        return presentedEffectIdStrings;
    }

    public void setPresentedEffectIdStrings(List<String> presentedEffectIdStrings) {
        this.presentedEffectIdStrings = presentedEffectIdStrings;
    }
}
