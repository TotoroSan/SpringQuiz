// QuizModifierService.java
package com.example.quiz.service.user;

import com.example.quiz.model.dto.QuizModifierDto;
import com.example.quiz.model.dto.QuizModifierEffectDto;
import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect;
import com.example.quiz.model.entity.QuizModifierEffectFactory;
import com.example.quiz.repository.QuizModifierRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public List<QuizModifierEffectDto> pickRandomModifierEffectDtos() {
        logger.info("Picking random modifier effects to present to the user");

        List<QuizModifierEffectDto> quizModifierEffectDtos =  QuizModifierEffectFactory.getQuizModifierEffectMetadataRegistry().values().stream()
                .limit(3)
                .map(metadata -> new QuizModifierEffectDto(
                        metadata.getIdString(),
                        metadata.getName(),
                        metadata.getDuration(), // Setting duration to 0 as it’s just for selection
                        metadata.getDescription())
                )
                .collect(Collectors.toList());

        logger.debug("Picked modifier effects: {}", quizModifierEffectDtos);

        return quizModifierEffectDtos;
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
        // todo this can be done more efficiently, if we jkust copy over non deleted entries and then set activeQuizModifierEffects = active list
        // Create a list of effects to be removed after iteration to avoid ConcurrentModificationException
        List<QuizModifierEffect> effectsToRemove = new ArrayList<>();

        // Iterate over all active effects
        for (QuizModifierEffect quizModifierEffect : activeQuizModifierEffects) {
            logger.debug("Processing ", quizModifierEffect.getIdString());
            // Reduce the duration by 1
            quizModifierEffect.decrementDuration();

            // If the effect duration has ended, add to remove list
            if (quizModifierEffect.getDuration() <= 0) {
                quizModifierEffect.reverse(quizModifier);  // Reverse the effect before removing
                effectsToRemove.add(quizModifierEffect);

                // Debug
                // debug
                logger.debug("Effect: ", quizModifierEffect.getIdString(), " added to removal list because duration is 0");
            }
        }

        // Remove expired effects from the list after iteration
        activeQuizModifierEffects.removeAll(effectsToRemove);
        logger.debug("All expired effects removed");

        // Persist the updated state TODO remove?
        quizModifierRepository.save(quizModifier);

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
                 getActiveModifierEffectDtos(quizModifier));

        logger.debug("QuizModifierDto successfully created");
        return quizModifierDto;
    }

    // Get all active modifier effects for a given quiz state.
    // This is called from within convertToDto to handle the conversion of effects to dto.
    // TODO this is actually the convertToDto function of the ModifierEffect class.
    //  relocate if dedicated modifierEffectsService is used
    public List<QuizModifierEffectDto> getActiveModifierEffectDtos(QuizModifier quizModifier) {
        logger.info("Getting QuizModifierEffectDtos for active modifierEffects");

        List<QuizModifierEffectDto>  quizModifierEffectDtos =  quizModifier.getActiveQuizModifierEffects().stream()
                .map(quizModifierEffect -> new QuizModifierEffectDto(
                        quizModifierEffect.getIdString(),
                        quizModifierEffect.getName(),
                        quizModifierEffect.getDuration(),
                        "Description for " + quizModifierEffect.getName())
                ).collect(Collectors.toList());


        logger.debug("Retreived ModifierEffectDtos for active modifiers: {}", quizModifierEffectDtos);

        return quizModifierEffectDtos;
    }
}

