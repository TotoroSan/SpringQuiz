package com.example.quiz.model.dto;

// do we need this annotaiton?
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
//@JsonSubTypes({
//        @JsonSubTypes.Type(value = QuestionGameEventDto.class, name = "QUESTION"),
//        @JsonSubTypes.Type(value = ModifierEffectsGameEventDto.class, name = "MODIFIER_EFFECTS")
//})


import com.example.quiz.model.enums.GameEventType;

/**
 * This is a "Wrapper" DTO for Game Events.
 * The frontend uses eventType (as an enum) to dynamically choose the correct components.
 */
public abstract class GameEventDto {

    private final GameEventType eventType;
    private long id;

    /**
     * Constructor for creating a GameEventDto with the given event type.
     *
     * @param eventType The type of the event, defined as a GameEventType enum.
     */
    public GameEventDto(GameEventType eventType) {
        this.eventType = eventType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public GameEventType getEventType() {
        return eventType;
    }

    @Override
    public String toString() {
        return "GameEventDto{" +
                "eventType=" + eventType +
                '}';
    }
}


