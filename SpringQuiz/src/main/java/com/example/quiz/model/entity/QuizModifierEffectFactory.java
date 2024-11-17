package com.example.quiz.model.entity;

// This is a factory class that serves the purpose of dynamically instantiating effect objects.
// It is needed, because the effects are instantiated after user request, so that only the chosen effect will be instantiated and subsequently saved.
// An alternative would be to have a "static" table with all effects in the database.

import java.util.HashMap;
import java.util.Map;

// TODO every new effect needs to be added here
// this is not an entity -> no need to persist the factory. move later for clarity.

public class QuizModifierEffectFactory {
    private static final Map<String, Class<? extends QuizModifierEffect>> effectRegistry = new HashMap<>();
    private static final Map<String, QuizModifierEffectMetaData> quizModifierEffectMetadataRegistry = new HashMap<>();

    static {
        effectRegistry.put("DOUBLE_SCORE", DoubleScoreQuizModifierEffect.class);
        effectRegistry.put("QUADRUPLE_SCORE", QuadrupleScoreQuizModifierEffect.class);
        effectRegistry.put("HIGH_DIFFICULTY", HighDifficultyQuizModifierEffect.class);

        // This registry holds static information about each subclass.
        // It is used for presenting static effect information to frontend without actually instantiating them
        quizModifierEffectMetadataRegistry.put("DOUBLE_SCORE", new QuizModifierEffectMetaData("DOUBLE_SCORE", "Double Score", "Description for Double Score", 3));
        quizModifierEffectMetadataRegistry.put("QUADRUPLE_SCORE", new QuizModifierEffectMetaData("QUADRUPLE_SCORE", "Quadruple Score", "Description for Quadruple Score", 3));
        quizModifierEffectMetadataRegistry.put("HIGH_DIFFICULTY", new QuizModifierEffectMetaData("HIGH_DIFFICULTY", "High Difficulty", "Description for High Difficulty", 3));
    }

    public static Map<String, Class<? extends QuizModifierEffect>> getEffectRegistry() {
        return effectRegistry;
    }
    public static Map<String, QuizModifierEffectMetaData> getQuizModifierEffectMetadataRegistry() {
        return quizModifierEffectMetadataRegistry;
    }
    public static QuizModifierEffect createEffect(String effectId, int duration, QuizModifier quizModifier) {
        Class<? extends QuizModifierEffect> effectClass = effectRegistry.get(effectId);
        if (effectClass != null) {
            try {
                return effectClass.getConstructor(int.class, QuizModifier.class).newInstance(duration, quizModifier);
            } catch (Exception e) {
                throw new RuntimeException("Error instantiating effect: " + effectId, e);
            }
        }
        throw new IllegalArgumentException("Unknown effect ID: " + effectId);
    }


}
