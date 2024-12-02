package com.example.quiz.model.dto;

import java.util.UUID;

public class QuizModifierEffectDto {


    private UUID uuid;
    private String idString; // this is is set by the subclasses and serves as identifier of the class
    private String name;
    private Integer duration;
    private String description;
    private String type;


    private Boolean isPermanent;
    private Integer rarity;


    private Integer tier;

    public QuizModifierEffectDto(UUID uuid, String idString, String name, Integer duration, String description, String type, Boolean isPermanent, Integer rarity, Integer tier) {
        this.setUuid(uuid);
        this.setIdString(idString);
        this.setName(name);
        this.setDuration(duration);
        this.setDescription(description);
        this.setType(type);
        this.setIsPermanent(isPermanent);
        this.setRarity(rarity);
        this.setTier(tier);
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsPermanent() {
        return isPermanent;
    }

    public void setIsPermanent(Boolean permanent) {
        isPermanent = permanent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        return "QuizModifierEffectDto{" +
                "id='" + idString + '\'' +
                ", name='" + name + '\'' +
                ", duration=" + duration +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", isPermanent=" + isPermanent +
                ", rarity=" + rarity +
                ", tier=" + tier +
                '}';
    }
}