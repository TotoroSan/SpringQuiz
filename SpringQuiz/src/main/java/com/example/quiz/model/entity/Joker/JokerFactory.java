package com.example.quiz.model.entity.Joker;

import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffectMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class JokerFactory {
    private static final Logger logger = LoggerFactory.getLogger(JokerFactory.class);

    // Registry mapping joker IDs to their corresponding Joker classes
    private static final Map<String, Class<? extends Joker>> JOKER_REGISTRY = new HashMap<>();

    // Registry holding static metadata for each joker; used for presentation and dynamic instantiation
    private static final Map<String, JokerMetaData> JOKER_METADATA_REGISTRY = new HashMap<>();

    // A static Random instance for generating random numbers (e.g., for tier rolling)
    private static final Random RANDOM = new Random();

    // Tier probabilities for rolling a tier for a Joker
    private static final Map<Integer, Integer> TIER_PROBABILITIES = Map.of(
            1, 50, // 50% chance for Tier 1
            2, 30, // 30% chance for Tier 2
            3, 15, // 15% chance for Tier 3
            4, 4,  // 4% chance for Tier 4
            5, 1   // 1% chance for Tier 5
    );

    static {
        // Register Joker subclasses
        JOKER_REGISTRY.put("SKIP_QUESTION", SkipQuestionJoker.class);
        JOKER_REGISTRY.put("FIFTY_FIFTY", FiftyFiftyJoker.class);
        JOKER_REGISTRY.put("TWENTYFIVE_SEVENTYFIVE", TwentyFiveSeventyFiveJoker.class);
        JOKER_REGISTRY.put("HINT", HintJoker.class);

        // Register Joker metadata (mock data, adjust as needed)
        JOKER_METADATA_REGISTRY.put("SKIP_QUESTION", new JokerMetaData("SKIP_QUESTION", "Skip Question", "Allows you to skip a question", 100, "gameplay", 2, 30, 1));
        JOKER_METADATA_REGISTRY.put("FIFTY_FIFTY", new JokerMetaData("FIFTY_FIFTY", "50/50", "Eliminates two wrong answers", 150, "gameplay", 3, 20,1 ));
        JOKER_METADATA_REGISTRY.put("TWENTYFIVE_SEVENTYFIVE", new JokerMetaData("TWENTYFIVE_SEVENTYFIVE", "25/75", "Eliminates one wrong answer", 120, "gameplay", 2, 25, 1));
        JOKER_METADATA_REGISTRY.put("HINT", new JokerMetaData("HINT", "Hint", "Provides a hint", 80, "gameplay", 1, 40, 1));
    }

    public static Map<String, JokerMetaData> getJokerMetadataRegistry() {
        return JOKER_METADATA_REGISTRY;
    }

    /**
     * Creates a Joker instance based on the provided joker ID and tier.
     * If the tier is null, a tier is rolled using the predefined tier probabilities.
     * The number of uses is determined by defaultUses (from metadata) multiplied by the effective tier.
     *
     * @param jokerId The joker identifier.
     * @param tier    The joker tier (if null, a tier will be rolled).
     * @return An instantiated Joker object.
     */
    public static Joker createJoker(String jokerId, Integer tier) {
        logger.info("Creating Joker with id: {} and tier: {}", jokerId, tier);
        JokerMetaData metaData = JOKER_METADATA_REGISTRY.get(jokerId);
        if (metaData == null) {
            throw new IllegalArgumentException("Unknown Joker ID: " + jokerId);
        }
        Class<? extends Joker> jokerClass = JOKER_REGISTRY.get(jokerId);
        if (jokerClass == null) {
            throw new IllegalArgumentException("No Joker class registered for ID: " + jokerId);
        }
        try {
            // If tier is null, roll a tier using rollTier()
            int effectiveTier = (tier != null) ? tier : rollTier();
            // Calculate the number of uses: defaultUses * effectiveTier TODO change tier effect here
            int uses = metaData.getUses() * effectiveTier;
            // Instantiate the Joker via reflection.
            // The constructor is expected to have the signature:
            // (String idString, String name, String description, int cost, Integer uses, Integer tier)
            return jokerClass.getConstructor(String.class, String.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class)
                    .newInstance(metaData.getIdString(), metaData.getName(), metaData.getDescription(), metaData.getCost(), uses, metaData.getRarity(), effectiveTier);
        } catch (Exception e) {
            throw new RuntimeException("Error instantiating Joker: " + jokerId, e);
        }
    }

    /**
     * Rolls a random tier based on the predefined tier probabilities.
     *
     * @return The rolled tier as an integer.
     */
    public static int rollTier() {
        int totalWeight = TIER_PROBABILITIES.values().stream().mapToInt(Integer::intValue).sum();
        int randomValue = RANDOM.nextInt(totalWeight);
        int cumulativeWeight = 0;
        for (Map.Entry<Integer, Integer> entry : TIER_PROBABILITIES.entrySet()) {
            cumulativeWeight += entry.getValue();
            if (randomValue < cumulativeWeight) {
                logger.info("Rolled tier: {}", entry.getKey());
                return entry.getKey();
            }
        }
        logger.debug("Tier roll fallback to 1");
        return 1;
    }

    /**
     * Rolls a random Joker based on rarity weights.
     *
     * @return The joker ID of the randomly selected joker.
     */
    public static String rollRandomJoker() {
        int totalWeight = JOKER_METADATA_REGISTRY.values().stream()
                .mapToInt(JokerMetaData::getRarityWeight)
                .sum();
        int randomValue = RANDOM.nextInt(totalWeight);
        int cumulativeWeight = 0;
        for (JokerMetaData metaData : JOKER_METADATA_REGISTRY.values()) {
            cumulativeWeight += metaData.getRarityWeight();
            if (randomValue < cumulativeWeight) {
                return metaData.getIdString();
            }
        }
        return "HINT"; // Fallback (should not occur)
    }
}
