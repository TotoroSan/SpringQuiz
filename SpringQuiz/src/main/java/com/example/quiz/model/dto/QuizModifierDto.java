package com.example.quiz.model.dto;

import java.util.ArrayList;
import java.util.List;

public class QuizModifierDto {

    private Long id;
    private double scoreMultiplier = 1.0;
    private int difficultyModifier = 1;

    private List<QuizModifierEffectDto> activeQuizModifierEffectDtos = new ArrayList<>();

    public QuizModifierDto() {
    }
    public QuizModifierDto(Long id, double scoreMultiplier, int difficultyModifier,
                           List<QuizModifierEffectDto> activeQuizModifierEffects) {
        this.difficultyModifier = difficultyModifier;
        this.scoreMultiplier = scoreMultiplier;
        this.id = id;
        this.activeQuizModifierEffectDtos = activeQuizModifierEffects;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getScoreMultiplier() {
        return scoreMultiplier;
    }

    public void setScoreMultiplier(double scoreMultiplier) {
        this.scoreMultiplier = scoreMultiplier;
    }

    public int getDifficultyModifier() {
        return difficultyModifier;
    }

    public void setDifficultyModifier(int difficultyModifier) {
        this.difficultyModifier = difficultyModifier;
    }

    public List<QuizModifierEffectDto> getActiveQuizModifierEffectDtos() {
        return activeQuizModifierEffectDtos;
    }

    public void setActiveQuizModifierEffectDtos(List<QuizModifierEffectDto> activeQuizModifierEffects) {
        this.activeQuizModifierEffectDtos = activeQuizModifierEffects;
    }


}