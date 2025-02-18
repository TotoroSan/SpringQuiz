package com.example.quiz.model.enums;

//TODO keep or nah?
public enum GameEventType {
    QUESTION("QUESTION"),
    MODIFIER_EFFECTS("MODIFIER_EFFECTS"),
    SHOP("SHOP");

    private final String value;

    GameEventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
