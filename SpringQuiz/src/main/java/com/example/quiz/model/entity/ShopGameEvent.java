package com.example.quiz.model.entity;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@DiscriminatorValue("SHOP")
@Table(name = "shop_game_event") // Creates a separate table
public class ShopGameEvent extends GameEvent {



    @ElementCollection
    @OrderColumn(name = "uuid_order")
    private List<UUID> presentedJokerUuids; // Stores the IDs of the presented joker (used for identification and validation afterwards)

    // IDs for the jokers presented to the user in the shop
    @ElementCollection
    @OrderColumn(name = "joker_id_order")
    private List<String> presentedJokerIds; // e.g. ["FIFTY_FIFTY", "SKIP_QUESTION"]

    @ElementCollection
    @OrderColumn(name = "name_order")
    private List<String> presentedJokerNames;

    @ElementCollection
    @OrderColumn(name = "cost_order")
    private List<Integer> presentedJokerCosts;

    @ElementCollection
    @OrderColumn(name = "rarity_order")
    private List<Integer> presentedJokerRarities;

    @ElementCollection
    @OrderColumn(name = "tier_order")
    private List<Integer> presentedJokerTiers;
    // You can store tier info if you want to show "Tier 2" in the shop, etc.

    // Constructors
    public ShopGameEvent() {
        super();
    }

    public ShopGameEvent(QuizState quizState,
                         List<UUID> presentedJokerUuids,
                         List<String> presentedJokerIds,
                         List<String> presentedJokerNames,
                         List<Integer> presentedJokerCosts,
                         List<Integer> presentedJokerRarities,
                         List<Integer> presentedJokerTiers) {
        super(quizState);
        this.presentedJokerUuids = presentedJokerUuids;
        this.presentedJokerIds = presentedJokerIds;
        this.presentedJokerNames = presentedJokerNames;
        this.presentedJokerCosts = presentedJokerCosts;
        this.presentedJokerRarities = presentedJokerRarities;
        this.presentedJokerTiers = presentedJokerTiers;
    }

    // Getters & setters
    public List<UUID> getPresentedJokerUuids() {
        return presentedJokerUuids;
    }

    public void setPresentedJokerUuids(List<UUID> presentedJokerUuids) {
        this.presentedJokerUuids = presentedJokerUuids;
    }

    public List<String> getPresentedJokerIds() {
        return presentedJokerIds;
    }

    public void setPresentedJokerIds(List<String> presentedJokerIds) {
        this.presentedJokerIds = presentedJokerIds;
    }

    public List<String> getPresentedJokerNames() {
        return presentedJokerNames;
    }

    public void setPresentedJokerNames(List<String> presentedJokerNames) {
        this.presentedJokerNames = presentedJokerNames;
    }

    public List<Integer> getPresentedJokerCosts() {
        return presentedJokerCosts;
    }

    public void setPresentedJokerCosts(List<Integer> presentedJokerCosts) {
        this.presentedJokerCosts = presentedJokerCosts;
    }

    public List<Integer> getPresentedJokerRarities() {
        return presentedJokerRarities;
    }

    public void setPresentedJokerRarities(List<Integer> presentedJokerRarities) {
        this.presentedJokerRarities = presentedJokerRarities;
    }

    public List<Integer> getPresentedJokerTiers() {
        return presentedJokerTiers;
    }

    public void setPresentedJokerTiers(List<Integer> presentedJokerTiers) {
        this.presentedJokerTiers = presentedJokerTiers;
    }
}