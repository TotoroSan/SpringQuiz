package com.example.quiz.model.entity;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@DiscriminatorValue("MODIFIER_EFFECTS")
@Table(name = "modifier_effects_game_event") // Creates a separate table
public class ModifierEffectsGameEvent extends GameEvent {

    // we only save the id strings and use them to create the presented effect dtos again
    // we need to do this because we do not instantiate / save the presented effects
    // we do not use ModififerEffectDto here because we do not want to save dtos but need to save some information for restoring on load. TODO maybe consolidate this
    // could also save the modifier effects but thats not good because then i am mixing
    // active modifier effects and selection modifier effects in one table

    // instead of having those  attribute lists i could also instantiate the effects and just have the effects in a list here.
    // but this would require us "saving" the instances in memory/cache until the user picks, which is suboptimal if we think scalingwise

    @ElementCollection
    @OrderColumn(name = "uuid_order")
    private List<UUID> presentedEffectUuids; // Stores the IDs of the presented modifier effects (used for identification and validation afterwards)


    @ElementCollection
    @OrderColumn(name = "id_order")
    private List<String> presentedEffectIdStrings; // Stores the IDs of the presented modifier effects


    @ElementCollection
    @OrderColumn(name = "id_order")
    private List<String> presentedEffectDescriptions; // Stores the IDs of the presented modifier effects

    @ElementCollection
    @OrderColumn(name = "tier_order")
    private List<Integer> presentedEffectTiers; // Stores the IDs of the presented modifier effects

    @ElementCollection
    @OrderColumn(name = "duration_order")
    private List<Integer> presentedEffectDurations; // Stores the IDs of the presented modifier effects

    // Constructors
    public ModifierEffectsGameEvent() {
        super();
    }

    // todo continue here , need to add the effect ids so i can use them for the dto, so we can identify the event later with the effect ids (are there effect ids if we dont instantiate?)
    public ModifierEffectsGameEvent(QuizState quizState, List<UUID> presentedEffectUuids,  List<String> presentedEffectIdStrings, List<String> presentedEffectDescriptions, List<Integer> presentedEffectTiers, List<Integer> presentedEffectDurations) {
        super(quizState);
        this.presentedEffectUuids = presentedEffectUuids;
        this.presentedEffectIdStrings = presentedEffectIdStrings;
        this.presentedEffectDescriptions = presentedEffectDescriptions;
        this.presentedEffectTiers = presentedEffectTiers;
        this.presentedEffectDurations = presentedEffectDurations;
    }

    // Getters and Setters
    public List<UUID> getPresentedEffectUuids() {
        return presentedEffectUuids;
    }

    public void setPresentedEffectUuids(List<UUID> presentedEffectUuids) {
        this.presentedEffectUuids = presentedEffectUuids;
    }

    public List<String> getPresentedEffectIdStrings() {
        return presentedEffectIdStrings;
    }

    public void setPresentedEffectIdStrings(List<String> presentedEffectIdStrings) {
        this.presentedEffectIdStrings = presentedEffectIdStrings;
    }

    public List<Integer> getPresentedEffectTiers() {
        return presentedEffectTiers;
    }

    public void setPresentedEffectTiers(List<Integer> presentedEffectTiers) {
        this.presentedEffectTiers = presentedEffectTiers;
    }

    public List<Integer> getPresentedEffectDurations() {
        return presentedEffectDurations;
    }

    public void setPresentedEffectDurations(List<Integer> presentedEffectDurations) {
        this.presentedEffectDurations = presentedEffectDurations;
    }

    public List<String> getPresentedEffectDescriptions() {
        return presentedEffectDescriptions;
    }

    public void setPresentedEffectDescriptions(List<String> presentedEffectDescriptions) {
        this.presentedEffectDescriptions = presentedEffectDescriptions;
    }

}
