package com.example.quiz.model.dto;

import com.example.quiz.model.dto.GameEventDto;
import com.example.quiz.model.dto.QuizModifierDto;
import com.example.quiz.model.dto.QuizModifierEffectDto;
import jakarta.persistence.*;
import java.util.List;

// game event dto that is used for displaying 3 modifier effects for selection
public class ModifierEffectsGameEventDto extends GameEventDto {

    private List<QuizModifierEffectDto> quizModifierEffectDtos; // Stores the IDs of the presented modifier effects

    // Constructors
    public ModifierEffectsGameEventDto() {
        super("MODIFIER_EFFECTS");
    }

    public ModifierEffectsGameEventDto(List<QuizModifierEffectDto> quizModifierEffectDtos) {
        super("MODIFIER_EFFECTS");
        this.quizModifierEffectDtos = quizModifierEffectDtos;
    }

    public List<QuizModifierEffectDto> getQuizModifierEffectDtos() {
        return quizModifierEffectDtos;
    }


}
