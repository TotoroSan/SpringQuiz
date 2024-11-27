package com.example.quiz.service.user;

import com.example.quiz.model.dto.*;
import com.example.quiz.model.entity.GameEvent;
import com.example.quiz.model.entity.ModifierEffectsGameEvent;
import com.example.quiz.model.entity.Question;
import com.example.quiz.model.entity.QuestionGameEvent;
import com.example.quiz.repository.AnswerRepository;
import com.example.quiz.repository.GameEventRepository;
import com.example.quiz.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
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

    public UserGameEventService() {

    }

    public GameEventDto convertToDto(GameEvent gameEvent) {
        logger.info("Trying to infer gameEvent subtype for conversion.");
        if (gameEvent instanceof QuestionGameEvent) {
            logger.info("Trying to initiate conversion for QuestionGameEvent");

            QuestionGameEvent questionGameEvent = (QuestionGameEvent) gameEvent;
            return convertToDto(questionGameEvent);
        } else if (gameEvent instanceof ModifierEffectsGameEvent) {
            logger.info("Trying to initiate conversion for ModifierEffectsGameEvent");
            ModifierEffectsGameEvent modifierEffectsEvent = (ModifierEffectsGameEvent) gameEvent;
            return convertToDto(modifierEffectsEvent);
        } else {
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

    private ModifierEffectsGameEventDto convertToDto(ModifierEffectsGameEvent modifierEffectsGameEvent) {
        logger.info("Converting questionGameEvent to modifierEffectsGameEventDto");
        // Retrieve Modifier Effects by their IDs todo need function that creates mofifiereffectrdtos from => has to access registry i think
        List<QuizModifierEffectDto> modifierEffects = modifierEffectsGameEvent.getPresentedEffectIdStrings().stream()
                .map(effectId -> userQuizModifierService.convertToDto(effectId))
                .collect(Collectors.toList());

        // Create and return the DTO
        return new ModifierEffectsGameEventDto(modifierEffects);
    }

}
