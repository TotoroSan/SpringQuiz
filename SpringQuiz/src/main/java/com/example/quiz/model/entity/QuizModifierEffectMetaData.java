package com.example.quiz.model.entity;

// Metadata holder class
// Note: instead of using a class like this, we could've also stored the information static in the QUizModifierEffect classes
// but this would maintanability worse
public class QuizModifierEffectMetaData {

    private final String idString;
    private final String name;
    private final String description;
    private int duration;

    public QuizModifierEffectMetaData(String idString, String name, String description, int duration) {
        this.idString = idString;
        this.name = name;
        this.description = description;
        this.duration = duration;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
