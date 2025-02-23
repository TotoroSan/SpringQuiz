package com.example.quiz.model.dto;


import com.example.quiz.model.enums.GameEventType;

import java.util.List;

public class ShopGameEventDto extends GameEventDto {

    private List<JokerDto> availableJokers;

    // No-arg constructor sets eventType to "SHOP"
    public ShopGameEventDto() {
        super(GameEventType.SHOP);
    }

    public ShopGameEventDto(List<JokerDto> availableJokers) {
        super(GameEventType.SHOP);
        this.availableJokers = availableJokers;
    }

    public List<JokerDto> getAvailableJokers() {
        return availableJokers;
    }

    public void setAvailableJokers(List<JokerDto> availableJokers) {
        this.availableJokers = availableJokers;
    }
}
