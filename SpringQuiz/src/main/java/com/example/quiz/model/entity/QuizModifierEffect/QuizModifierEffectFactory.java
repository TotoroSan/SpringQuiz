package com.example.quiz.model.entity.QuizModifierEffect;

// This is a factory class that serves the purpose of dynamically instantiating effect objects.
// It is needed, because the effects are instantiated after user request, so that only the chosen effect will be instantiated and subsequently saved.
// An alternative would be to have a "static" table with all effects in the database.

import com.example.quiz.model.entity.*;
import com.example.quiz.model.entity.QuizModifierEffect.DifficultyQuizModifierEffect.HighDifficultyQuizModifierEffect;
import com.example.quiz.model.entity.QuizModifierEffect.DifficultyQuizModifierEffect.MaxDifficultyLimitQuizModifierEffect;
import com.example.quiz.model.entity.QuizModifierEffect.DifficultyQuizModifierEffect.MinDifficultyLimitQuizModifierEffect;
import com.example.quiz.model.entity.QuizModifierEffect.LifeQuizModifierEffect.IncreaseLifeCounterQuizModifierEffect;
import com.example.quiz.model.entity.QuizModifierEffect.ScoreQuizModifierEffect.IncreaseScoreMultiplierQuizModifierEffect;
import com.example.quiz.model.entity.QuizModifierEffect.TopicQuizModifierEffect.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

// TODO every new effect needs to be added here
// this is not an entity -> no need to persist the factory. move later for clarity.

public class QuizModifierEffectFactory {
    private static final Logger logger = LoggerFactory.getLogger(QuizModifierEffectFactory.class);

    // this registry is used to dynamically instantiate modifier classes based on user input
    private static final Map<String, Class<? extends QuizModifierEffect>> QUIZMODIFIER_EFFECT_REGISTRY = new HashMap<>();

    // This registry holds static information about each subclass. It is used for presenting static effect information
    // to frontend without actually instantiating them (could also use static fields in modifer classes for this)
    private static final Map<String, QuizModifierEffectMetaData> QUIZ_MODIFIER_EFFECT_META_DATA_REGISTRY = new HashMap<>();
    private static final List<String> TOPIC_REGISTRY;
    private static final Map<Integer, Integer> TIER_PROBABILITIES;

    // not sure if this stays here. static random instance that is reused across all calls
    private static final Random RANDOM = new Random();


    static {
        QUIZMODIFIER_EFFECT_REGISTRY.put("INCREASE_SCORE_MULTIPLIER", IncreaseScoreMultiplierQuizModifierEffect.class);

        QUIZMODIFIER_EFFECT_REGISTRY.put("HIGH_DIFFICULTY", HighDifficultyQuizModifierEffect.class);
        QUIZMODIFIER_EFFECT_REGISTRY.put("MAX_DIFFICULTY_LIMIT", MaxDifficultyLimitQuizModifierEffect.class);
        QUIZMODIFIER_EFFECT_REGISTRY.put("MIN_DIFFICULTY_LIMIT", MinDifficultyLimitQuizModifierEffect.class);
        // topic modifier effects
        QUIZMODIFIER_EFFECT_REGISTRY.put("CHOOSE_TOPIC", ChooseTopicQuizModifierEffect.class);
        // life counter effects
        QUIZMODIFIER_EFFECT_REGISTRY.put("INCREASE_LIFE_COUNTER", IncreaseLifeCounterQuizModifierEffect.class);


        // METADATA REGISTRY

        // Score effects
        QUIZ_MODIFIER_EFFECT_META_DATA_REGISTRY.put("INCREASE_SCORE_MULTIPLIER", new QuizModifierEffectMetaData("INCREASE_SCORE_MULTIPLIER", "Increase Score Multiplier", "The next questions give more score.", 3, "score", false, 1, 50));
        // Difficulty effects
        QUIZ_MODIFIER_EFFECT_META_DATA_REGISTRY.put("HIGH_DIFFICULTY", new QuizModifierEffectMetaData("HIGH_DIFFICULTY", "High Difficulty", "The next questions will be of higher difficulty.", 3, "difficulty", false, 1, 50));
        QUIZ_MODIFIER_EFFECT_META_DATA_REGISTRY.put("MAX_DIFFICULTY_LIMIT", new QuizModifierEffectMetaData("MAX_DIFFICULTY_LIMIT", "Max Difficulty Limit", "The next questions will have a given max difficulty.", 3, "difficulty", false, 1, 50));
        QUIZ_MODIFIER_EFFECT_META_DATA_REGISTRY.put("MIN_DIFFICULTY_LIMIT", new QuizModifierEffectMetaData("MIN_DIFFICULTY_LIMIT", "Min Difficulty Limit", "The next questions will have a given min difficulty.", 3, "difficulty", false, 1, 50));
        // topic modifier effects
        QUIZ_MODIFIER_EFFECT_META_DATA_REGISTRY.put("CHOOSE_TOPIC", new QuizModifierEffectMetaData("CHOOSE_TOPIC", "Choose Topic", "Allows you to choose the topic for the next questions.", 3, "topic", false, 1, 50));
        // life counter effects this would need to be a permanent effect though
        QUIZ_MODIFIER_EFFECT_META_DATA_REGISTRY.put("INCREASE_LIFE_COUNTER", new QuizModifierEffectMetaData("INCREASE_LIFE_COUNTER", "Increase Life Counter", "Increases your life count by 1.", null, "life", true, 1, 50));


        TOPIC_REGISTRY = List.of(
                "Art",
                "Astronomy",
                "Biology",
                "Chemistry",
                "Economics",
                "Geography",
                "Geology",
                "History",
                "Linguistics",
                "Literature",
                "Mathematics",
                "Medicine",
                "Music",
                "Mythology",
                "Physics",
                "Science",
                "Sports",
                "Technology",
                "Politics",
                "Culture"
        );

        // Define tier probabilities (can also move to a class-level constant or configuration file or service logic)
        TIER_PROBABILITIES = Map.of(
                1, 50, // 50% chance for Tier 1
                2, 30, // 30% chance for Tier 2
                3, 15, // 15% chance for Tier 3
                4, 4,  // 4% chance for Tier 4
                5, 1   // 1% chance for Tier 5
        );


    }

    public static Map<String, Class<? extends QuizModifierEffect>> getEffectRegistry() {
        return QUIZMODIFIER_EFFECT_REGISTRY;
    }
    public static Map<String, QuizModifierEffectMetaData> getQuizModifierEffectMetadataRegistry() {
        return QUIZ_MODIFIER_EFFECT_META_DATA_REGISTRY;
    }

    public static List<String> getTopicRegistry() {
        return TOPIC_REGISTRY;
    }

    // instantiates an effect by id string.
    // topic effects are instantiated with e.g. CHOOSE_TOPIC_MEDICINE as effectId
    // todo replace duration with tier here (duration is indecritly influenced by tier)
    public static QuizModifierEffect createEffect(String effectId, int duration, QuizModifier quizModifier, Integer tier) {
        logger.info("Effect Factory is instantiating effect from effectId: {}", effectId);

        try {
            // Special case: CHOOSE_TOPIC effects (need this because it has differing parameters to other effects)
            if (effectId.startsWith("CHOOSE_TOPIC_")) {
                logger.info("Effect_id has CHOOSE_TOPIC_ prefix");

                String topic = effectId.substring("CHOOSE_TOPIC_".length()); // Extract topic
                logger.info("Extracted topic suffix: {}", topic);

                Class<? extends QuizModifierEffect> effectClass = QUIZMODIFIER_EFFECT_REGISTRY.get("CHOOSE_TOPIC");
                if (effectClass != null) {
                    logger.info("Instantiating class");
                    return effectClass.getConstructor(QuizModifier.class, int.class, String.class, Integer.class)
                            .newInstance(quizModifier, duration, topic, tier);
                }
            }

            // General case: Look up by exact effectId
            Class<? extends QuizModifierEffect> effectClass = QUIZMODIFIER_EFFECT_REGISTRY.get(effectId);
            if (effectClass != null) {
                logger.info("Instantiating class");
                return effectClass.getConstructor(QuizModifier.class, int.class, Integer.class)
                        .newInstance(quizModifier, duration, tier);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error instantiating effect: " + effectId, e);
        }

        throw new IllegalArgumentException("Unknown effect ID: " + effectId);
    }


    public static String getRandomTopic() {
        return TOPIC_REGISTRY.get(RANDOM.nextInt(TOPIC_REGISTRY.size()));
    }

    public static int rollTier() {
        int totalWeight = TIER_PROBABILITIES.values().stream().mapToInt(Integer::intValue).sum();
        int randomValue = RANDOM.nextInt(totalWeight);
        int cumulativeWeight = 0;

        for (Map.Entry<Integer, Integer> entry : TIER_PROBABILITIES.entrySet()) {
            cumulativeWeight += entry.getValue();
            if (randomValue < cumulativeWeight) {
                return entry.getKey();
            }
        }

        // Default fallback (shouldn't be reached)
        return 1;
    }

}
