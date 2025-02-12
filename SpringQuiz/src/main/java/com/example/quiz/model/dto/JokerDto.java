package com.example.quiz.model.dto;

import java.util.UUID;

public class JokerDto {

    private UUID uuid;
    private String idString; // Identifier string set by the Joker subclasses
    private String name;
    private String description;
    private int cost;
    private Integer uses;   // Number of uses available
    private Integer rarity; // The rarity of the Joker
    private Integer tier;   // The tier of the Joker


    public JokerDto(UUID id, String idString, String name, String description, int cost, Integer uses, Integer tier, Integer rarity) {
        this.uuid = id;
        this.idString = idString;
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.uses = uses;
        this.rarity = rarity;
        this.tier = tier;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getIdString() {
        return idString;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Integer getUses() {
        return uses;
    }

    public void setUses(Integer uses) {
        this.uses = uses;
    }

    public Integer getTier() {
        return tier;
    }

    public void setTier(Integer tier) {
        this.tier = tier;
    }

    public Integer getRarity() {
        return rarity;
    }

    public void setRarity(Integer rarity) {
        this.rarity = rarity;
    }

    @Override
    public String toString() {
        return "JokerDto{" +
                "id=" + uuid +
                ", idString='" + idString + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", cost=" + cost +
                ", uses=" + uses +
                ", tier=" + tier +
                ", rarity=" + rarity +
                '}';
    }
}
