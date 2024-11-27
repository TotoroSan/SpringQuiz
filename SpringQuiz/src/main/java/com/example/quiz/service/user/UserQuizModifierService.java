// QuizModifierService.java
package com.example.quiz.service.user;

import com.example.quiz.model.dto.GameEventDto;
import com.example.quiz.model.dto.QuizModifierDto;
import com.example.quiz.model.dto.QuizModifierEffectDto;
import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffectFactory;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffectMetaData;
import com.example.quiz.repository.QuizModifierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserQuizModifierService {
    private static final Logger logger = LoggerFactory.getLogger(UserQuizModifierService.class);


    @Autowired
    private QuizModifierRepository quizModifierRepository;

    // todo check if randomness requirement is met
    // Returns a list of X (currently 3) random QuizModifierEffectDtos
    // Todo if we want to display more modifiers, change here (either introduce parameter or change in code)
    // todo can be done more efficiently
    public List<QuizModifierEffectDto> pickRandomModifierEffectDtos() {
        logger.info("Picking random modifier effects to present to the user");

        List<QuizModifierEffectDto> quizModifierEffectDtos = QuizModifierEffectFactory.getQuizModifierEffectMetadataRegistry().values().stream()
                .map(metadata -> new QuizModifierEffectDto(
                        metadata.getIdString(),
                        metadata.getName(),
                        metadata.getDuration(), // Setting duration to 0 as it’s just for selection
                        metadata.getDescription(),
                        metadata.getType(),
                        metadata.getPermanent(),
                        metadata.getRarity())
                )
                .collect(Collectors.toList());

        // Shuffle the list to pick random elements
        Collections.shuffle(quizModifierEffectDtos);

        // Limit the list to 3 random elements
        List<QuizModifierEffectDto> randomQuizModifierEffectDtos = quizModifierEffectDtos.stream()
                .limit(3)
                .collect(Collectors.toList());

        logger.debug("Picked modifier effects: {}", randomQuizModifierEffectDtos);

        return randomQuizModifierEffectDtos;
    }

    //@Transactional
    public boolean applyModifierEffectById(QuizModifier quizModifier, String idString) {
        // TODO: 2 is only placeholder here, overload to also have function with custom duration
        logger.info("Instantiating and Applying quizModifierEffect with id ", idString);
        QuizModifierEffect quizModifierEffect = QuizModifierEffectFactory.createEffect(idString, 2, quizModifier);

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
                 getActiveModifierEffectDtos(quizModifier), quizModifier.getLifeCounter());

        logger.debug("QuizModifierDto successfully created");
        return quizModifierDto;
    }

    // todo  relocate if dedicated modifierEffectsService is used
    public QuizModifierEffectDto convertToDto(QuizModifierEffect quizModifierEffect) {
        logger.info("Converting QuizModifierEffect to QuizModifierEffectDto");

        QuizModifierEffectDto quizModifierEffectDto = new QuizModifierEffectDto(quizModifierEffect.getIdString(),
                quizModifierEffect.getName(),
                quizModifierEffect.getDuration(),
                quizModifierEffect.getDescription(),
                quizModifierEffect.getType(),
                quizModifierEffect.getPermanent(),
                quizModifierEffect.getRarity());

        logger.debug("QuizModifierEffectDto successfully created");
        return quizModifierEffectDto;
    }

    // todo bandaid fix: create a QuizModifierDto from id string (using the registry)
    // todo: this is needed to recreate the last ModifierEffectsGameEvents

    // this is used to convert a non instantiated effect to dto to present for selection
    public QuizModifierEffectDto convertToDto(String idString) {
        logger.info("Converting QuizModifierEffect idString to QuizModifierEffectDto");
        QuizModifierEffectMetaData quizModifierEffectMetaData = QuizModifierEffectFactory.getQuizModifierEffectMetadataRegistry().get(idString);

        QuizModifierEffectDto quizModifierEffectDto = new QuizModifierEffectDto(quizModifierEffectMetaData.getIdString(),
                quizModifierEffectMetaData.getName(),
                quizModifierEffectMetaData.getDuration(),
                quizModifierEffectMetaData.getDescription(),
                quizModifierEffectMetaData.getType(),
                quizModifierEffectMetaData.getPermanent(),
                quizModifierEffectMetaData.getRarity());

        logger.debug("QuizModifierEffectDto successfully created");
        return quizModifierEffectDto;
    }

    // Get all active modifier effects for a given quiz state.
    // This is called from within convertToDto to handle the conversion of effects to dto.
    public List<QuizModifierEffectDto> getActiveModifierEffectDtos(QuizModifier quizModifier) {
        logger.info("Getting QuizModifierEffectDtos for active modifierEffects");

        List<QuizModifierEffectDto>  quizModifierEffectDtos =  quizModifier.getActiveQuizModifierEffects().stream()
                .map(quizModifierEffect -> convertToDto(quizModifierEffect)
                ).collect(Collectors.toList());


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

