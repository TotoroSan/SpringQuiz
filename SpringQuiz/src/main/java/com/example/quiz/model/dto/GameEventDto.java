package com.example.quiz.model.dto;

// do we need this annotaiton?
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
//@JsonSubTypes({
//        @JsonSubTypes.Type(value = QuestionGameEventDto.class, name = "QUESTION"),
//        @JsonSubTypes.Type(value = ModifierEffectsGameEventDto.class, name = "MODIFIER_EFFECTS")
//})


// this is a "Wrapper" Dto for Game Events
// the frontend uses eventType to dynamically choose the correct components
public abstract class GameEventDto {

    private final String eventType;
    private long id;

    // Constructor for a question event
    public GameEventDto(String eventType) {
        this.eventType = eventType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    // Getters and setters
    public String getEventType() {
        return eventType;
    }

    @Override
    public String toString() {
        return "GameEventDto{" +
                "eventType='" + eventType + '\'' +
                '}';
    }
}

