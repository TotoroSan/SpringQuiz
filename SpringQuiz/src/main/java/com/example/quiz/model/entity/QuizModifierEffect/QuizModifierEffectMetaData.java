package com.example.quiz.model.entity.QuizModifierEffect;

// Metadata holder class
// Note: instead of using a class like this, we could've also stored the information static in the QUizModifierEffect classes
// but this would maintanability worse
public class QuizModifierEffectMetaData {

    private final String idString;
    private final String name;
    private final String description;
    private Integer duration;
    private String type;
    private Boolean isPermanent;



    private Integer rarity;

    public QuizModifierEffectMetaData(String idString, String name, String description, Integer duration, String type, Boolean isPermanent, Integer rarity) {
        this.idString = idString;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.type = type;
        this.isPermanent = isPermanent;
        this.rarity = rarity;
    }

    public String getIdString() {
        return idString;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getPermanent() {
        return isPermanent;
    }

    public void setPermanent(Boolean permanent) {
        isPermanent = permanent;
    }

    public Integer getRarity() {
        return rarity;
    }

    public void setRarity(Integer rarity) {
        this.rarity = rarity;
    }
}
