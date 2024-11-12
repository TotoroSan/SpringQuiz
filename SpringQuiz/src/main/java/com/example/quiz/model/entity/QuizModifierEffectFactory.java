package com.example.quiz.model.entity;

// This is a factory class that serves the purpose of dynamically instantiating effect objects.
// It is needed, because the effects are instantiated after user request, so that only the chosen effect will be instantiated and subsequently saved.
// An alternative would be to have a "static" table with all effects in the database.

import jakarta.persistence.Entity;

import java.util.HashMap;
import java.util.Map;

// TODO every new effect needs to be added here
// this is not an entity -> no need to persist the factory. move later for clarity.

public class QuizModifierEffectFactory {
    private static final Map<String, Class<? extends QuizModifierEffect>> effectRegistry = new HashMap<>();

    static {
        effectRegistry.put("DOUBLE_SCORE", DoubleScoreQuizModifierEffect.class);
        effectRegistry.put("QUADRUPLE_SCORE", QuadrupleScoreQuizModifierEffect.class);
        effectRegistry.put("HIGH_DIFFICULTY", HighDifficultyQuizModifierEffect.class);
    }

    public static Map<String, Class<? extends QuizModifierEffect>> getEffectRegistry() {
        return effectRegistry;
    }

    public static QuizModifierEffect createEffect(String effectId, int duration) {
        Class<? extends QuizModifierEffect> effectClass = effectRegistry.get(effectId);
        if (effectClass != null) {
            try {
                return effectClass.getConstructor(int.class).newInstance(duration);
            } catch (Exception e) {
                throw new RuntimeException("Error instantiating effect: " + effectId, e);
            }
        }
        throw new IllegalArgumentException("Unknown effect ID: " + effectId);
    }
}
