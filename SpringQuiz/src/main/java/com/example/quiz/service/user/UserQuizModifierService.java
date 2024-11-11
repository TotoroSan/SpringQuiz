// QuizModifierService.java
package com.example.quiz.service.user;

import com.example.quiz.model.dto.QuizModifierEffectDto;
import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect;
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
	
	
    private final Map<String, QuizModifierEffect> quizModifierEffectMap = new HashMap<>();
    
    @Autowired
    private QuizModifierEffectRepository quizModifierEffectRepository;
    @Autowired
    private QuizModifierRepository quizModifierRepository;
    
    @Autowired
    public UserQuizModifierService(QuizModifierEffectRepository repository) {
        this.quizModifierEffectRepository = repository;
        System.out.println("UserQuizModifierService initialized");
        populateQuizModifierEffectMap();
    }

    //@Transactional
    public void populateQuizModifierEffectMap() {
        List<QuizModifierEffect> allQuizModifierEffects = quizModifierEffectRepository.findAll();
        allQuizModifierEffects.forEach(effect -> quizModifierEffectMap.put(effect.getIdString(), effect));
        
        // debugging 
        for (QuizModifierEffect eff : allQuizModifierEffects) {
        	System.out.println("effect " + eff.getIdString());
        }
    }


    public void applyModifier(QuizModifier quizModifier, QuizModifierEffect quizModifierEffect) {
        quizModifierEffect.apply(quizModifier);
    }

    public List<QuizModifierEffectDto> pickRandomModifierDtos() {
        return new ArrayList<>(quizModifierEffectMap.values()).stream()
                .limit(3)
                .map(quizModifierEffect -> new QuizModifierEffectDto(quizModifierEffect.getIdString(), quizModifierEffect.getName(), quizModifierEffect.getDuration(), "Description for " + quizModifierEffect.getName()))
                .collect(Collectors.toList());
    }

    public boolean applyModifierById(QuizState quizState, String idString) {
        QuizModifierEffect quizModifierEffect = quizModifierEffectMap.get(idString);

        if (quizModifierEffect != null) {
            quizModifierEffect.apply(quizState.getQuizModifier());
            addModifierEffect(quizState.getQuizModifier(), quizModifierEffect);
            
            // Persist the change to the database
            quizModifierRepository.save(quizState.getQuizModifier());
            return true;
        }
        return false;
    }
    
    // todo how do i get a effect by id 
    public void removeExpiredModifierEffectIds(QuizModifier quizModifier) {
        quizModifier.getActiveQuizModifierEffects().removeIf(effect -> quizModifierEffectMap.get(effect).getDuration() <= 0);
    }
    
    public void addModifierEffect(QuizModifier quizModifier, QuizModifierEffect quizModifierEffect) {
        quizModifier.getActiveQuizModifierEffects().add(quizModifierEffect);
    }
    
    public List<QuizModifierEffect> getActiveModifierEffects(QuizModifier quizModifier) {
        List<QuizModifierEffect> activeModifierEffectIds = quizModifier.getActiveQuizModifierEffects();
        List<QuizModifierEffect> activeModifierEffects = activeModifierEffectIds.stream()
                .map(quizModifierEffectMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        
        return activeModifierEffects;
    }


}

