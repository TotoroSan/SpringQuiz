// QuizModifierService.java
package com.example.quiz.service.user;

import com.example.quiz.model.dto.QuizModifierEffectDto;
import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect;
import com.example.quiz.model.entity.QuizModifierEffectFactory;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.repository.QuizModifierEffectRepository;
import com.example.quiz.repository.QuizModifierRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserQuizModifierService {


    @Autowired
    private QuizModifierRepository quizModifierRepository;
    

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
    public boolean applyModifierById(QuizState quizState, String idString) {
        // TODO: 5 is only placeholder here, overload to also have function with custom duration
        QuizModifierEffect quizModifierEffect = QuizModifierEffectFactory.createEffect(idString, 5);

        if (quizModifierEffect != null) {
            quizModifierEffect.apply(quizState.getQuizModifier());
            addModifierEffect(quizState.getQuizModifier(), quizModifierEffect);
            // Persist the change to the database
            quizModifierRepository.save(quizState.getQuizModifier());
            return true;
        }
        return false;
    }
    

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

