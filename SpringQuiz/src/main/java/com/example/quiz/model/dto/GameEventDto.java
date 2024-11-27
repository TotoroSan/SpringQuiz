package com.example.quiz.model.dto;

import com.example.quiz.model.entity.ModifierEffectsGameEvent;
import com.example.quiz.model.entity.QuestionGameEvent;

import java.util.List;

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

    // Constructor for a question event TODO continue here
    public GameEventDto(String eventType) {
        this.eventType = eventType;
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

