// QuizModifierService.java
package com.example.quiz.service.user;

import com.example.quiz.model.dto.QuizModifierDto;
import com.example.quiz.model.dto.QuizModifierEffectDto;
import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffectFactory;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffectMetaData;
import com.example.quiz.repository.GameEventRepository;
import com.example.quiz.repository.QuizModifierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserQuizModifierService {
    private static final Logger logger = LoggerFactory.getLogger(UserQuizModifierService.class);


    @Autowired
    private QuizModifierRepository quizModifierRepository;

    @Autowired
    private GameEventRepository gameEventRepository;

    // todo check if randomness requirement is met
    // Returns a list of X (currently 3) random QuizModifierEffectDtos
    // Todo if we want to display more modifiers, change here (either introduce parameter or change in code)
    // todo can be done more efficiently

    // todo: introduce weights for different modifier classes, pick specific classes with that weight then roll the tier (rarity of an effect and tier are 2 different things, currently tier is only duration)
    // todo: all the weight information / configurable parameters can be found in the effect factory class
    public List<QuizModifierEffectDto> pickRandomModifierEffectDtos() {
        logger.info("Picking random modifier effects with weighted probabilities");

        // Fetch all metadata from the registry
        ArrayList<QuizModifierEffectMetaData> allEffects = new ArrayList<>(QuizModifierEffectFactory.getQuizModifierEffectMetadataRegistry().values());

        List<QuizModifierEffectDto> selectedEffects = new ArrayList<>();
        Random random = new Random();


        // effect picker rolls a random number X € (1,SUM(rarityWeight)).  the effect in which ragne this number falls is picked
        // (e.g. first uncommon effect (1,50) second uncommon effect (51,100), rare effect (101, 115))
        // this way the probability of drawing an effect is proportional to its rarityWeight
        // Ensure we can pick up to 3 effects via weighted effect picking
        for (int i = 0; i < 3 && !allEffects.isEmpty(); i++) {
            // Calculate total weight
            int totalWeight = allEffects.stream()
                    .mapToInt(QuizModifierEffectMetaData::getRarityWeight)
                    .sum();

            // Pick a random value in range (1, totalWeight)
            int randomValue = random.nextInt(totalWeight);
            // Iterate to find the selected effect
            int cumulativeWeight = 0;
            QuizModifierEffectMetaData chosenEffect = null;

            for (QuizModifierEffectMetaData effect : allEffects) {
                cumulativeWeight += effect.getRarityWeight();
                if (randomValue < cumulativeWeight) {
                    chosenEffect = effect;
                    logger.info("Chosen ModifierEffect: {}", chosenEffect.getIdString());
                    break;
                }
            }

            // todo here we would also need to roll the tier for the effect
            if (chosenEffect != null) {
                logger.info("Chosen ModifierEffect is of type: {}", chosenEffect.getType());

                // Adjust description or other fields based on tier
                int rolledTier = QuizModifierEffectFactory.rollTier();
                logger.info("Rolled tier: {}", rolledTier);


                if ("topic".equalsIgnoreCase(chosenEffect.getType())) {
                    String randomTopic = QuizModifierEffectFactory.getRandomTopic();
                    logger.info("Chosen topic is: {}", randomTopic);

                    String effectId = chosenEffect.getIdString() + "_" + randomTopic.toUpperCase();
                    logger.info("Created effectIdString: {}", effectId);

                    String descriptionWithTopic = chosenEffect.getDescription() + " (Topic: " + randomTopic + ")";

                    selectedEffects.add(new QuizModifierEffectDto(
                            UUID.randomUUID(),
                            effectId, // Append topic to ID
                            chosenEffect.getName(),
                            randomizeDuration(chosenEffect), // todo this is our "fake" tiering for now (depreceated)
                            descriptionWithTopic,
                            chosenEffect.getType(),
                            chosenEffect.getPermanent(),
                            chosenEffect.getRarity(),
                            rolledTier
                    ));
                } else {
                    selectedEffects.add(new QuizModifierEffectDto(
                            UUID.randomUUID(),
                            chosenEffect.getIdString(),
                            chosenEffect.getName(),
                            randomizeDuration(chosenEffect), // todo think about how we tie this to tiers
                            chosenEffect.getDescription(),
                            chosenEffect.getType(),
                            chosenEffect.getPermanent(),
                            chosenEffect.getRarity(),
                            rolledTier
                    ));
                }

                // Remove the chosen effect from the pool
                allEffects.remove(chosenEffect);
            }
        }

        logger.debug("Selected modifier effects: {}", selectedEffects);

        return selectedEffects;
    }

    // Optional: Adjust the duration based on rarity or other factors
    private int randomizeDuration(QuizModifierEffectMetaData effect) {
        int minDuration = 2; // Tier 1
        int maxDuration = 8; // Tier 5
        return minDuration + (effect.getRarity() - 1) * (maxDuration - minDuration) / 4; // Scale duration
    }




    //@Transactional
    public boolean applyModifierEffectByIdString(QuizModifier quizModifier, String idString, Integer duration, Integer tier) {
        // TODO: 2 is only placeholder here, overload to also have function with custom duration
        logger.info("Instantiating and Applying quizModifierEffect with id: {}", idString);
        QuizModifierEffect quizModifierEffect = QuizModifierEffectFactory.createEffect(idString, duration, quizModifier, tier);

        if (quizModifierEffect != null) {
            quizModifierEffect.apply(quizModifier);
            quizModifier.addActiveQuizModifierEffect(quizModifierEffect);

            // debug
            logger.debug("quizModifierEffect successfully applied and added to activeModifiers", quizModifierEffect.getIdString());
            return true;
        }

        // debug
        logger.debug("No quizModifierEffect could be instantiated");
        return false;
    }





    public void processActiveQuizModifierEffectsForNewRound(QuizModifier quizModifier) {
        logger.info("Processing ActiveQuizModifierEffects for new round for quizModifier: ", quizModifier);

        List<QuizModifierEffect> activeQuizModifierEffects = quizModifier.getActiveQuizModifierEffects();

        // Create a list of effects to be removed after iteration to avoid ConcurrentModificationException
        List<QuizModifierEffect> effectsToRemove = new ArrayList<>();

        // Iterate over all active effects
        for (QuizModifierEffect quizModifierEffect : activeQuizModifierEffects) {
            logger.debug("Processing ", quizModifierEffect.getIdString());

            // if the effect is temporary
            if (quizModifierEffect.getPermanent() == false) {
                // Reduce the duration by 1
                quizModifierEffect.decrementDuration();

                // If the effect duration has ended, add to remove list
                if (quizModifierEffect.getDuration() <= 0) {
                    quizModifierEffect.reverse(quizModifier);  // Reverse the effect before removing
                    effectsToRemove.add(quizModifierEffect);

                    // Debug
                    logger.debug("Effect: ", quizModifierEffect.getIdString(), " added to removal list because duration is 0");
                }
            }
        }

        // Remove expired effects from the list after iteration
        activeQuizModifierEffects.removeAll(effectsToRemove);
        logger.debug("All expired effects removed");
    }

    // currently not in use (check for removal)
    public void removeExpiredModifierEffectIds(QuizModifier quizModifier) {
        // if modifier duration is <= 0 remove the modifier and reverse it's effect
        quizModifier.getActiveQuizModifierEffects().removeIf(effect -> {
            if (effect.getDuration() <= 0) {
                effect.reverse(quizModifier);
                return true;
            }
            return false;
        });

        // Save changes to the database
        quizModifierRepository.save(quizModifier);
    }


    // convert QuizModifier to QuizModifierDto (data transfer object)
    // This is called during creating quizStateDto
    public QuizModifierDto convertToDto(QuizModifier quizModifier) {
        logger.info("Converting QuizModifier to QuizModifierDto");

        QuizModifierDto quizModifierDto = new QuizModifierDto(quizModifier.getId(),
                quizModifier.getScoreMultiplier(), quizModifier.getDifficultyModifier(),
                 getActiveModifierEffectDtos(quizModifier), quizModifier.getLifeCounter(), quizModifier.getCash(),
                quizModifier.getCashMultiplier(), quizModifier.getBaseCashReward());

        logger.debug("QuizModifierDto successfully created");
        return quizModifierDto;
    }

    // todo  relocate if dedicated modifierEffectsService is used
    public QuizModifierEffectDto convertToDto(QuizModifierEffect quizModifierEffect) {
        logger.info("Converting QuizModifierEffect: {}", quizModifierEffect.getIdString(), "to QuizModifierEffectDto");

        QuizModifierEffectDto quizModifierEffectDto = new QuizModifierEffectDto(
                UUID.randomUUID(),  // todo this is a placeholder, think about if we need to pass the actual id
                quizModifierEffect.getIdString(),
                quizModifierEffect.getName(),
                quizModifierEffect.getDuration(),
                quizModifierEffect.getDescription(),
                quizModifierEffect.getType(),
                quizModifierEffect.getPermanent(),
                quizModifierEffect.getRarity(),
                quizModifierEffect.getTier()
        );

        logger.debug("QuizModifierEffectDto successfully created");
        return quizModifierEffectDto;
    }

    // todo bandaid fix: create a QuizModifierDto from id string (using the registry)
    // todo: this is needed to recreate the last ModifierEffectsGameEvents

    // this is used to convert a non instantiated effect to dto to present for selection
    // wee need special handling for topic effects here because we only keep CHOOSE_TOPIC in the registry
    // we carry the topic info directly in the string (random picker puts out CHOOSE_TOPIC_MEDICINE)
    // because otherwise we would need to pass an argument or keep all topic subclasses in the registry
    // we decided to do it like this to keep adding and removing topics easy (just need to add to topic registry)
    // TODO maybe rename this? as it is no conversion but rather a creation
    public QuizModifierEffectDto convertToDto(UUID uuid, String idString, Integer tier, Integer duration) {
        logger.info("Converting QuizModifierEffect: {} to QuizModifierEffectDto", idString);

        // Check for topic-based effects
        if (idString.startsWith("CHOOSE_TOPIC_")) {
            logger.info("Detected topic-based effect: {}", idString);

            // Extract the base ID and topic
            String baseId = "CHOOSE_TOPIC";
            String topic = idString.substring("CHOOSE_TOPIC_".length());

            // Fetch metadata for base ID
            QuizModifierEffectMetaData quizModifierEffectMetaData = QuizModifierEffectFactory.getQuizModifierEffectMetadataRegistry().get(baseId);

            if (quizModifierEffectMetaData == null) {
                logger.error("Metadata not found for base ID: {}", baseId);
                throw new IllegalArgumentException("Invalid effect ID: " + idString);
            }

            // Append the topic to the description and ID in the DTO
            String descriptionWithTopic = quizModifierEffectMetaData.getDescription() + " (Topic: " + topic + ")";
            QuizModifierEffectDto quizModifierEffectDto = new QuizModifierEffectDto(
                    uuid,
                    idString, // Full ID with topic
                    quizModifierEffectMetaData.getName(),
                    duration,
                    descriptionWithTopic,
                    quizModifierEffectMetaData.getType(),
                    quizModifierEffectMetaData.getPermanent(),
                    quizModifierEffectMetaData.getRarity(),
                    tier
            );

            logger.info("QuizModifierEffectDto successfully created for topic-based effect");
            return quizModifierEffectDto;
        }

        // Default case for non-topic effects
        QuizModifierEffectMetaData quizModifierEffectMetaData = QuizModifierEffectFactory.getQuizModifierEffectMetadataRegistry().get(idString);

        if (quizModifierEffectMetaData == null) {
            logger.error("Metadata not found for effect ID: {}", idString);
            throw new IllegalArgumentException("Invalid effect ID: " + idString);
        }

        QuizModifierEffectDto quizModifierEffectDto = new QuizModifierEffectDto(
                uuid,
                quizModifierEffectMetaData.getIdString(),
                quizModifierEffectMetaData.getName(),
                duration,
                quizModifierEffectMetaData.getDescription(),
                quizModifierEffectMetaData.getType(),
                quizModifierEffectMetaData.getPermanent(),
                quizModifierEffectMetaData.getRarity(),
                tier
        );

        logger.info("QuizModifierEffectDto successfully created for non-topic effect");
        return quizModifierEffectDto;
    }


    // Get all active modifier effects for a given quiz state.
    // This is called from within convertToDto to handle the conversion of effects to dto.
    public List<QuizModifierEffectDto> getActiveModifierEffectDtos(QuizModifier quizModifier) {
        logger.info("Getting QuizModifierEffectDtos for active modifierEffects");

        List<QuizModifierEffectDto>  quizModifierEffectDtos =  quizModifier.getActiveQuizModifierEffects().stream()
                .map(quizModifierEffect -> {
                    logger.debug("Converting active QuizModifierEffect with idString: {}", quizModifierEffect.getIdString());
                    return convertToDto(quizModifierEffect);
                }).collect(Collectors.toList());


        logger.debug("Retreived ModifierEffectDtos for active modifiers: {}", quizModifierEffectDtos);

        return quizModifierEffectDtos;
    }

    public void incrementLifeCounter(QuizModifier quizModifier){
        incrementLifeCounter(quizModifier, 1);
    }

    public void incrementLifeCounter(QuizModifier quizModifier, int increments){
        quizModifier.setLifeCounter(quizModifier.getLifeCounter() + increments);
    }

    public void decrementLifeCounter(QuizModifier quizModifier){
        decrementLifeCounter(quizModifier, 1);
    }

    public void decrementLifeCounter(QuizModifier quizModifier, int decrements){
        quizModifier.setLifeCounter(quizModifier.getLifeCounter() - decrements);
    }


}

