package com.example.quiz.service.user;

import com.example.quiz.model.dto.*;
import com.example.quiz.model.entity.GameEvent;
import com.example.quiz.model.entity.ModifierEffectsGameEvent;
import com.example.quiz.model.entity.QuestionGameEvent;
import com.example.quiz.model.entity.ShopGameEvent;
import com.example.quiz.repository.GameEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// todo currently a "helper" service. actual deciding on which event will be used is in UserQuizState currentlyx
// game events are "wrappers" for attributes that are needed for a specific game component
// (e.g. QuestionGameEvent contains everything the frontend needs to display a question component)
@Service
public class UserGameEventService {
    private static final Logger logger = LoggerFactory.getLogger(UserGameEventService.class);

    @Autowired
    private UserAnswerService userAnswerService;

    @Autowired
    private UserQuizModifierService userQuizModifierService;

    @Autowired
    private GameEventRepository gameEventRepository;

    public UserGameEventService() {

    }

    public GameEventDto convertToDto(GameEvent gameEvent) {
        logger.info("Trying to infer gameEvent subtype for conversion.");
        if (gameEvent instanceof QuestionGameEvent questionGameEvent) {
            logger.info("Trying to initiate conversion for QuestionGameEvent");
            return convertToDto(questionGameEvent);
        } else if (gameEvent instanceof ModifierEffectsGameEvent modifierEffectsEvent) {
            logger.info("Trying to initiate conversion for ModifierEffectsGameEvent");
            return convertToDto(modifierEffectsEvent);
        } else if (gameEvent instanceof ShopGameEvent shopGameEvent) {
            logger.info("Trying to initiate conversion for ShopGameEvent");
            return convertToDto(shopGameEvent);
        }
        else {
            throw new IllegalArgumentException("Unknown game event type: " + gameEvent.getClass());
        }
    }

    private QuestionGameEventDto convertToDto(QuestionGameEvent questionGameEvent) {
        logger.info("Converting questionGameEvent to questionGameEventDto");

        // Copy the Question and Shuffled Answers directly from QuestionGameEvent
        Long questionId = questionGameEvent.getQuestionId();
        String questionText = questionGameEvent.getQuestionText();

        List<AnswerDto> shuffledAnswerDtos = questionGameEvent.getShuffledAnswers().stream()
                .map(answer -> userAnswerService.convertToDto(answer))
                .collect(Collectors.toList());

        // create questionGameEventDto
        QuestionGameEventDto questionGameEventDto = new QuestionGameEventDto(questionText, questionId, shuffledAnswerDtos);

        // initialize generalized wrapper dto
        // Create and return the DTO
        return questionGameEventDto;
    }

    // this is used to extract info from our save object (modifierEffectsGameEvent)
    private ModifierEffectsGameEventDto convertToDto(ModifierEffectsGameEvent modifierEffectsGameEvent) {
        logger.info("Converting modifierEffectsGameEvent to ModifierEffectsGameEventDto");

        // Extract the presented effect details
        List<UUID> effectUuids = modifierEffectsGameEvent.getPresentedEffectUuids();
        List<String> effectDescriptions = modifierEffectsGameEvent.getPresentedEffectDescriptions();
        List<String> effectIdStrings = modifierEffectsGameEvent.getPresentedEffectIdStrings();
        List<Integer> effectTiers = modifierEffectsGameEvent.getPresentedEffectTiers();
        List<Integer> effectDurations = modifierEffectsGameEvent.getPresentedEffectDurations();

        if (effectIdStrings.size() != effectTiers.size() || effectIdStrings.size() != effectDurations.size()) {
            logger.error("Mismatch in sizes of presentedEffectIdStrings, tiers, or durations lists");
            throw new IllegalStateException("Mismatch in sizes of presentedEffectIdStrings, tiers, or durations lists");
        }

        // Combine the three lists to create a list of QuizModifierEffectDto objects
        List<QuizModifierEffectDto> modifierEffectDtos = new ArrayList<>();
        for (int i = 0; i < effectIdStrings.size(); i++) {
            UUID effectUuid = effectUuids.get(i);
            String effectIdString = effectIdStrings.get(i);
            String effectDescription = effectDescriptions.get(i);
            Integer tier = effectTiers.get(i);
            Integer duration = effectDurations.get(i);

            try {
                // Convert to DTO using the updated convertToDto method
                QuizModifierEffectDto dto = userQuizModifierService.convertToDto(effectUuid, effectIdString, effectDescription, tier, duration);
                modifierEffectDtos.add(dto);
            } catch (Exception e) {
                logger.error("Error converting effect ID: {} to QuizModifierEffectDto", effectIdString, e);
            }
        }

        // Create and return the ModifierEffectsGameEventDto
        ModifierEffectsGameEventDto modifierEffectsGameEventDto = new ModifierEffectsGameEventDto(modifierEffectDtos);
        logger.info("Successfully converted modifierEffectsGameEvent to ModifierEffectsGameEventDto");
        return modifierEffectsGameEventDto;
    }

    public ShopGameEventDto convertToDto(ShopGameEvent shopGameEvent) {
        logger.info("Converting ShopGameEvent to ShopGameEventDto");

        // We'll build a list of JokerDto objects from the parallel lists in the entity
        List<JokerDto> jokerDtos = new ArrayList<>();

        List<String> idStrings = shopGameEvent.getPresentedJokerIds();
        List<String> names = shopGameEvent.getPresentedJokerNames();
        List<Integer> costs = shopGameEvent.getPresentedJokerCosts();
        List<Integer> rarities = shopGameEvent.getPresentedJokerRarities();
        List<Integer> tiers = shopGameEvent.getPresentedJokerTiers();

        // Safeguard in case sizes differ
        int size = idStrings.size();
        for (int i = 0; i < size; i++) {
            String idString = idStrings.get(i);
            String name = names.get(i);
            Integer cost = costs.get(i);
            Integer rarity = rarities.get(i);
            Integer tier = tiers.get(i);

            // Build a JokerDto TODO this one is a seperate function for the modifier effects, maybe also do here
            JokerDto jokerDto = new JokerDto(
                    null,            // no persistent ID yet
                    idString,
                    name,
                    "Purchase this joker to enhance your game!", // or a more meaningful description
                    (cost != null ? cost : 0),
                    1, // default uses?
                    tier,
                    rarity
            );
            jokerDtos.add(jokerDto);
        }

        ShopGameEventDto shopGameEventDto = new ShopGameEventDto(jokerDtos);
        logger.info("Created ShopGameEventDto with {} jokers", jokerDtos.size());
        return shopGameEventDto;
    }


    public void resolveGameEvent(Long gameEventId) {
        GameEvent gameEvent = gameEventRepository.findById(gameEventId)
                .orElseThrow(() -> new IllegalArgumentException("GameEvent not found"));

        if (gameEvent.isConsumed()) {
            throw new IllegalStateException("GameEvent has already been resolved");
        }

        gameEvent.setConsumed(true);
        gameEventRepository.save(gameEvent);
    }

}
