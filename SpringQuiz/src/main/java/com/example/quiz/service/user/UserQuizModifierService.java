// QuizModifierService.java
package com.example.quiz.service.user;

import com.example.quiz.model.dto.QuizModifierEffectDto;
import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect;
import com.example.quiz.model.entity.QuizModifierEffectFactory;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.repository.QuizModifierRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserQuizModifierService {


    @Autowired
    private QuizModifierRepository quizModifierRepository;

    @Autowired
    private UserQuizStateService userQuizStateService;
    

    public void applyModifier(QuizModifier quizModifier, QuizModifierEffect quizModifierEffect) {
        quizModifierEffect.apply(quizModifier);
    }

    public List<QuizModifierEffectDto> pickRandomModifierDtos() {
        return new ArrayList<>(QuizModifierEffectFactory.getEffectRegistry().keySet()).stream()
                .limit(3)
                .map(effectId -> {
                    Class<? extends QuizModifierEffect> effectClass = QuizModifierEffectFactory.getEffectRegistry().get(effectId);
                    try {
                        QuizModifierEffect effect = effectClass.getDeclaredConstructor().newInstance();
                        return new QuizModifierEffectDto(effect.getIdString(), effect.getName(), effect.getDuration(), "Description for " + effect.getName());
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to create effect instance for DTO", e);
                    }
                })
                .collect(Collectors.toList());
    }

    //@Transactional
    public boolean applyModifierById(QuizModifier quizModifier, String idString) {
        // TODO: 5 is only placeholder here, overload to also have function with custom duration
        QuizModifierEffect quizModifierEffect = QuizModifierEffectFactory.createEffect(idString, 2);

        if (quizModifierEffect != null) {
            quizModifierEffect.apply(quizModifier);
            addModifierEffect(quizModifier, quizModifierEffect);



            // Persist the change to the database
            quizModifierRepository.save(quizModifier);
            return true;
        }
        return false;
    }

    public void processModifierEffectsForNewRound(QuizModifier quizModifier) {
        List<QuizModifierEffect> activeQuizModifierEffects = getActiveModifierEffects(quizModifier);

        // Create a list of effects to be removed after iteration to avoid ConcurrentModificationException
        List<QuizModifierEffect> effectsToRemove = new ArrayList<>();

        // Iterate over all active effects
        for (QuizModifierEffect quizModifierEffect : activeQuizModifierEffects) {
            // Reduce the duration by 1
            quizModifierEffect.decrementDuration();
            System.out.println("Decrementing effect duration of: " + quizModifierEffect.getIdString());

            // If the effect duration has ended, add to remove list
            if (quizModifierEffect.getDuration() <= 0) {
                quizModifierEffect.reverse(quizModifier);  // Reverse the effect before removing
                effectsToRemove.add(quizModifierEffect);

                // Debug
                System.out.println("Effect: " + quizModifierEffect.getIdString() + " removed because duration is 0");
            }
        }

        // Remove expired effects from the list after iteration
        activeQuizModifierEffects.removeAll(effectsToRemove);

        // Persist the updated state
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
    
    public void addModifierEffect(QuizModifier quizModifier, QuizModifierEffect quizModifierEffect) {
        quizModifier.getActiveQuizModifierEffects().add(quizModifierEffect);
    }
    
    public List<QuizModifierEffect> getActiveModifierEffects(QuizModifier quizModifier) {
        return quizModifier.getActiveQuizModifierEffects();
    }
}

