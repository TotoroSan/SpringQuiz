package com.example.quiz.model.dto;

import com.example.quiz.model.enums.GameEventType;

import java.util.List;

// game event dto that is used for displaying 3 modifier effects for selection
public class ModifierEffectsGameEventDto extends GameEventDto {

    private List<QuizModifierEffectDto> quizModifierEffectDtos; // Stores the IDs of the presented modifier effects

    // Constructors
    public ModifierEffectsGameEventDto() {
        super(GameEventType.MODIFIER_EFFECTS);
    }

    public ModifierEffectsGameEventDto(List<QuizModifierEffectDto> quizModifierEffectDtos) {
        super(GameEventType.MODIFIER_EFFECTS);
        this.quizModifierEffectDtos = quizModifierEffectDtos;
    }

    public List<QuizModifierEffectDto> getQuizModifierEffectDtos() {
        return quizModifierEffectDtos;
    }


    @Override
    public String toString() {
        return "ModifierEffectsGameEventDto{" +
                "quizModifierEffectDtos=" + quizModifierEffectDtos +
                '}';
    }
}
