package com.example.quiz.service.admin;

import com.example.quiz.model.dto.JokerDto;
import com.example.quiz.model.entity.GameEvent;
import com.example.quiz.model.entity.Joker.*;
import com.example.quiz.model.entity.QuestionGameEvent;
import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.repository.QuizStateRepository;
import com.example.quiz.service.user.UserGameEventService;
import com.example.quiz.service.user.UserJokerService;
import com.example.quiz.service.user.UserQuizStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class AdminGameEventService {

    private final QuizStateRepository quizStateRepository;
    private final UserGameEventService userGameEventService;
    private final UserJokerService userJokerService;
    private final UserQuizStateService userQuizStateService; // Corrected service

    @Autowired
    public AdminGameEventService(QuizStateRepository quizStateRepository,
                                 UserGameEventService userGameEventService,
                                 UserJokerService userJokerService,
                                 UserQuizStateService userQuizStateService) { // Corrected service
        this.quizStateRepository = quizStateRepository;
        this.userGameEventService = userGameEventService;
        this.userJokerService = userJokerService;
        this.userQuizStateService = userQuizStateService; // Corrected service
    }

    @Transactional
    public void triggerShopEvent(QuizState quizState) {
        checkAdminPermission();
        userQuizStateService.createShopGameEvent(quizState); // Delegate to UserQuizStateService
        // No save needed: handled by transaction and cascading.
    }

    @Transactional
    public void triggerQuestionEvent(QuizState quizState, Integer difficulty) {
        checkAdminPermission();
        userQuizStateService.createQuestionGameEvent(quizState); // Delegate to userQuizStateService
        // No save needed
    }

    @Transactional
    public void triggerModifierEvent(QuizState quizState) {
        checkAdminPermission();
        userQuizStateService.createModifierEffectsGameEvent(quizState); // Correct: UserQuizStateService
        // No save needed
    }

    @Transactional
    public QuizState resetQuizState(Long userId) { //Changed method signature.
        checkAdminPermission();
        // 1. End the current quiz (if any)
        Optional<QuizState> latestActiveQuizState = userQuizStateService.getLatestActiveQuizStateByUserId(userId);
        latestActiveQuizState.ifPresent(userQuizStateService::processQuizEnd); // Use method reference

        // 2. Start a *new* quiz
        return userQuizStateService.startNewQuiz(userId); // Return the new QuizState
    }



    @Transactional
    public JokerDto addJokerToQuizState(QuizState quizState, JokerDto jokerDto) {
        checkAdminPermission();
        Joker joker = JokerFactory.createJoker(quizState, jokerDto.getIdString(), jokerDto.getTier());
        if (joker == null) {
            throw new IllegalArgumentException("Failed to create joker with ID: " + jokerDto.getIdString());
        }
        quizState.getOwnedJokers().put(joker.getId(), joker);
        quizStateRepository.save(quizState);  // Explicit save
        return userJokerService.convertToDto(joker);
    }

    @Transactional
    public void removeJokerFromQuizState(QuizState quizState, UUID jokerId) {
        checkAdminPermission();
        if (!quizState.getOwnedJokers().containsKey(jokerId)) {
            throw new IllegalArgumentException("Joker with given ID does not exist.");
        }
        quizState.getOwnedJokers().remove(jokerId);
        quizStateRepository.save(quizState);

    }


    @Transactional
    public QuizModifier modifyQuizModifier(QuizState quizState, QuizModifier updatedModifier) {
        checkAdminPermission();
        //Copy properties from updatedModifier to existing modifier (using setters)
        QuizModifier existingModifier = quizState.getQuizModifier();
        existingModifier.setCash(updatedModifier.getCash());
        existingModifier.setLifeCounter(updatedModifier.getLifeCounter());
        existingModifier.setScoreMultiplier(updatedModifier.getScoreMultiplier());
        // ... copy other properties ...

        quizStateRepository.save(quizState);  //save is needed

        return existingModifier; // Return the *updated* modifier
    }


    // Keep this method, and keep it private!  It's an internal implementation detail.
    private QuizState getQuizStateOrThrow(Long quizStateId) {
        return quizStateRepository.findById(quizStateId)
                .orElseThrow(() -> new IllegalArgumentException("QuizState not found with ID: " + quizStateId));
    }
    //Keep this too
    public QuizState getQuizState(Long quizStateId) {
        checkAdminPermission(); // Add security check
        return getQuizStateOrThrow(quizStateId);
    }

    // Added security check method
    private void checkAdminPermission() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                !authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) { //Check for role admin
            throw new AccessDeniedException("Insufficient permissions");
        }
    }

    @Transactional
    public String testJokerEffect(QuizState quizState, String jokerType) {
        checkAdminPermission();

        // Ensure there's a question event to apply the effect to. If not, create one.
        GameEvent latestEvent = quizState.getLatestGameEvent();
        if (!(latestEvent instanceof QuestionGameEvent)) {
            userQuizStateService.createQuestionGameEvent(quizState); // Create a question event
            latestEvent = quizState.getLatestGameEvent(); // Get the newly created event
        }


        Joker joker = JokerFactory.createJoker(quizState, jokerType, 1);
        if (joker == null) {
            return "Cannot create Joker of type: " + jokerType;
        }

        boolean result = false;
        if (joker instanceof FiftyFiftyJoker) {
            result = userJokerService.applyFiftyFiftyJoker(quizState, joker);
        } else if (joker instanceof SkipQuestionJoker) {
            result = userJokerService.applySkipQuestionJoker(quizState, joker);
        } else if (joker instanceof TwentyFiveSeventyFiveJoker) {
            result = userJokerService.applyTwentyFiveSeventyFiveJoker(quizState, joker);
        } else {
            return "Unsupported joker type for effect testing";
        }

        if (result) {
            quizStateRepository.save(quizState);
            return "Joker effect applied successfully";
        } else {
            return "Failed to apply joker effect";
        }
    }
}