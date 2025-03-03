package com.example.quiz.service.user;

import com.example.quiz.model.dto.*;
import com.example.quiz.model.entity.*;
import com.example.quiz.model.enums.GameEventType;
import com.example.quiz.repository.QuizStateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserQuizStateService {
    private static final Logger logger = LoggerFactory.getLogger(UserQuizStateService.class);

    // responsible for actions that modify the state, like incrementing the question index.

    // TODO FIXME BUG XXX HACK OPTIMIZE (= builtin comment annotations)

    // by stating autowired the object creation gets handed over to spring.
    // it is an "automatic" connection to another class that is needed and has a connection to our class. we basically "wire" the classes -> if we create a quizservice we always reate a quizrepository via that wire
    // without spring i would need to create the repository in the constructor and delete it after the operation or on end of connection

    @Autowired
    private QuizStateRepository quizStateRepository;

    @Autowired
    private UserQuestionService userQuestionService;

    @Autowired
    private UserQuizModifierService userQuizModifierService;

    @Autowired
    private UserJokerService userJokerService;

    @Autowired
    private UserGameEventService userGameEventService;


    // This should be transactional i think
    // Initialize quiz with questions fetched from the QuestionService
    // todo move the clearing of old states to a dedicated function (might run periodically)
    /**
     * Starts a new quiz session for a given user.
     * <p>
     * If there is an existing active quiz, it is invalidated before creating a new one.
     * </p>
     *
     * @param userId The ID of the user starting a new quiz.
     * @return The newly created {@link QuizState}.
     */
    public QuizState startNewQuiz(Long userId) {
        logger.info("Starting new Quiz (creating new QuizState) for user: {}", userId);

        // Find all active quiz states for the user (only one quiz can be active at a time currently)
        List<QuizState> activeQuizStates = quizStateRepository.findAllByUserIdAndIsActiveTrue(userId);

        // Invalidate all active quiz states
        for (QuizState activeState : activeQuizStates) {
            logger.info("Clearing old QuizStates...");
            activeState.setActive(false);
            activeState.clearGameEvents(); // Clear associated GameEvents
            quizStateRepository.save(activeState);
        }

        // Create and save new quiz state
        QuizState quizState = new QuizState(userId);
        logger.debug("Successfully created new QuizState for user: {}", userId);

        return quizStateRepository.save(quizState);
    }

    /**
     * Retrieves the history of past quiz states for a given user.
     *
     * @param userId The ID of the user whose quiz history is requested.
     * @return A list of past {@link QuizState} objects.
     */
    public List<QuizState> getQuizStateHistory(Long userId) {
        return quizStateRepository.findAllByUserIdAndIsActiveFalse(userId);
    }

    /**
     * Retrieves the most recent quiz state for a given user.
     *
     * @param userId The ID of the user whose latest quiz state is requested.
     * @return An {@link Optional} containing the latest {@link QuizState}, if available.
     */
    public Optional<QuizState> getLatestQuizStateByUserId(Long userId) {
        return quizStateRepository.findFirstByUserIdOrderByIdDesc(userId);
    }

    /**
     * Retrieves the latest active quiz state for a given user.
     *
     * @param userId The ID of the user whose active quiz state is requested.
     * @return An {@link Optional} containing the latest active {@link QuizState}, if available.
     */
    public Optional<QuizState> getLatestActiveQuizStateByUserId(Long userId) {
        return quizStateRepository.findFirstByUserIdAndIsActiveIsTrueOrderByIdDesc(userId);
    }

    /**
     * Retrieves all quiz states for a given user.
     *
     * @param userId The ID of the user.
     * @return An {@link Optional} containing all {@link QuizState} objects for the user.
     */
    public Optional<QuizState> getAllQuizStatesByUserId(Long userId) {
        return quizStateRepository.findByUserId(userId);
    }

    /**
     * Saves the given quiz state to the database.
     *
     * @param quizState The {@link QuizState} to be saved.
     */
    public void saveQuizState(QuizState quizState) {
        quizStateRepository.save(quizState);
    }

    /**
     * Retrieves the next unanswered question for the current quiz session.
     *
     * @param quizState The current quiz state.
     * @return The next {@link Question} or {@code null} if there are no more questions.
     */
    public Question getNextQuestion(QuizState quizState) {
        if (!hasMoreQuestions(quizState)) {
            return null;  // No more questions available
        }
        Question currentQuestion = getCurrentQuestion(quizState);
        quizState.setCurrentQuestionIndex(quizState.getCurrentQuestionIndex() + 1);  // Move to the next question
        saveQuizState(quizState);
        return currentQuestion;
    }

    /**
     * Adds a new question to the quiz state.
     *
     * @param quizState The current quiz state.
     * @param question  The {@link Question} to be added.
     */
    public void addQuestion(QuizState quizState, Question question) {
        logger.info("Adding question {} to quizState", question.getId());

        quizState.getAllQuestions().add(question);
        incrementCurrentQuestionIndex(quizState);
        saveQuizState(quizState);

        logger.debug("Added question {} successfully", question.getId());
    }

    /**
     * Converts a {@link QuizState} entity into a {@link QuizStateDto}.
     *
     * @param quizState The quiz state to be converted.
     * @return A DTO representation of the quiz state.
     */
    public QuizStateDto convertToDto(QuizState quizState) {
        logger.info("Converting quizState to QuizStateDto");

        QuizStateDto quizStateDto = new QuizStateDto(
                quizState.getScore(),
                quizState.getCurrentRound(),
                quizState.getAllQuestions().isEmpty()
                        ? null
                        : quizState.getAllQuestions().get(quizState.getCurrentQuestionIndex()).getQuestionText(),
                userQuizModifierService.convertToDto(quizState.getQuizModifier()),
                userJokerService.getOwnedJokerDtos(quizState),
                quizState.isActive()
        );

        logger.debug("Successfully converted QuizState to QuizStateDto");
        return quizStateDto;
    }

    /**
     * Increments the current question index to move to the next question in the quiz.
     *
     * @param quizState The current quiz state.
     */
    public void incrementCurrentQuestionIndex(QuizState quizState) {
        quizState.setCurrentQuestionIndex(quizState.getCurrentQuestionIndex() + 1);
    }

    /**
     * Increments the quiz score by 1, taking into account the score multiplier.
     *
     * @param quizState The current quiz state.
     */
    public void incrementScore(QuizState quizState) {
        quizState.setScore(quizState.getScore() + (quizState.getQuizModifier().getScoreMultiplier() * 1));
        saveQuizState(quizState);
    }

    /**
     * Increments the quiz score by a specified amount, considering the score multiplier.
     *
     * @param quizState The current quiz state.
     * @param increments The number of increments to be added to the score.
     */
    public void incrementScore(QuizState quizState, int increments) {
        quizState.setScore(quizState.getScore() + (quizState.getQuizModifier().getScoreMultiplier() * increments));
        saveQuizState(quizState);
    }

    /**
     * Increments the current round in the quiz state.
     *
     * @param quizState The current quiz state.
     */
    public void incrementCurrentRound(QuizState quizState) {
        quizState.setCurrentRound(quizState.getCurrentRound() + 1);
    }

    /**
     * Marks a question as completed by adding its ID to the completed questions list.
     *
     * @param quizState The current quiz state.
     * @param questionId The ID of the question to mark as completed.
     */
    public void markQuestionAsCompleted(QuizState quizState, Long questionId) {
        quizState.getCompletedQuestionIds().add(questionId);
    }

    /**
     * Retrieves the current question based on the quiz state's question index.
     *
     * @param quizState The current quiz state.
     * @return The current {@link Question}, or {@code null} if no question is available.
     */
    public Question getCurrentQuestion(QuizState quizState) {
        logger.info("Retrieving current question from quizState");
        if (quizState.getCurrentQuestionIndex() < quizState.getAllQuestions().size()) {
            return quizState.getAllQuestions().get(quizState.getCurrentQuestionIndex());
        }

        logger.debug("Retrieval of current question from quizState failed");
        return null;
    }

    /**
     * Checks whether a specific question has been completed.
     *
     * @param quizState The current quiz state.
     * @param questionId The ID of the question to check.
     * @return {@code true} if the question is completed, otherwise {@code false}.
     */
    public boolean isCompleted(QuizState quizState, Long questionId) {
        return quizState.getCompletedQuestionIds().contains(questionId);
    }

    /**
     * Determines whether there are more questions remaining in the quiz.
     *
     * @param quizState The current quiz state.
     * @return {@code true} if there are more questions, otherwise {@code false}.
     */
    public boolean hasMoreQuestions(QuizState quizState) {
        return quizState.getCurrentQuestionIndex() < quizState.getAllQuestions().size();
    }

    /**
     * Moves to the next segment by resetting the answered question count and clearing event tracking.
     *
     * @param quizState The current quiz state.
     */
    public void moveToNextSegment(QuizState quizState) {
        quizState.setCurrentSegment(quizState.getCurrentSegment() + 1);
        quizState.setAnsweredQuestionsInSegment(1);
        quizState.clearSegmentEventTypes();
        saveQuizState(quizState);
    }

    /**
     * Increments the count of answered questions in the current segment.
     *
     * @param quizState The current quiz state.
     */
    public void incrementAnsweredQuestionsInSegment(QuizState quizState) {
        quizState.setAnsweredQuestionsInSegment(quizState.getAnsweredQuestionsInSegment() + 1);
    }

    /**
     * Processes a correct answer submission by updating the quiz state.
     * <p>
     * This method marks the question as completed, increments the score and round,
     * updates active modifier effects, and adds cash based on multipliers.
     * </p>
     *
     * @param quizState The current quiz state.
     */
    public void processCorrectAnswerSubmission(QuizState quizState) {
        logger.info("Processing correct answer submission for QuizState ID: {}", quizState.getId());
        QuizModifier quizModifier = quizState.getQuizModifier();

        // Update quizState
        markQuestionAsCompleted(quizState, getCurrentQuestion(quizState).getId());
        incrementScore(quizState, getCurrentQuestion(quizState).getDifficulty()); // Score = Difficulty
        incrementCurrentRound(quizState);
        incrementAnsweredQuestionsInSegment(quizState);

        // Update active QuizModifierEffects
        userQuizModifierService.processActiveQuizModifierEffectsForNewRound(quizModifier);

        // Calculate and add earned cash
        int cashEarned = (int) (quizModifier.getBaseCashReward() *
                quizModifier.getCashMultiplier() * getCurrentQuestion(quizState).getDifficulty());
        quizModifier.addCash(cashEarned);

        logger.info("Added {} cash (after multiplier) to QuizState ID: {}", cashEarned, quizState.getId());

        // Persist the updated quiz state
        saveQuizState(quizState);

        logger.debug("Successfully processed correct answer submission for QuizState ID: {}", quizState.getId());
    }




    /**
     * Processes the skipping of a question. A skipped question is marked as completed,
     * the current round and answered counter are advanced, but no points, cash, or lives are affected.
     *
     * @param quizState The current quiz state.
     */
    public void processSkipQuestionSubmission(QuizState quizState) {
        logger.info("Processing skip question submission for QuizState ID: {}", quizState.getId());

        // Mark the current question as completed
        Question currentQuestion = getCurrentQuestion(quizState);
        if (currentQuestion != null) {
            markQuestionAsCompleted(quizState, currentQuestion.getId());
        } else {
            logger.warn("No current question found; cannot mark as completed.");
        }

        // Advance round and increment questions in segment without awarding points or cash
        incrementCurrentRound(quizState);
        incrementAnsweredQuestionsInSegment(quizState);

        // Persist the updated quiz state
        saveQuizState(quizState);

        logger.debug("Successfully processed skip question submission for QuizState ID: {}", quizState.getId());
    }


    /**
     * Processes an incorrect answer submission by decrementing the life counter.
     * If the user has no lives left, the quiz ends.
     *
     * @param quizState The current quiz state.
     */
    public void processIncorrectAnswerSubmission(QuizState quizState) {
        logger.info("Processing incorrect answer for QuizState ID: {}", quizState.getId());

        userQuizModifierService.decrementLifeCounter(quizState.getQuizModifier());

        if (quizState.getQuizModifier().getLifeCounter() <= 0) {
            logger.info("No lives left, initiating quiz end.");
            processQuizEnd(quizState);
        }

        saveQuizState(quizState);
        logger.debug("Successfully processed incorrect answer for QuizState ID: {}", quizState.getId());
    }

    /**
     * Ends the quiz by marking it inactive and clearing game-related data.
     * Active modifier effects and game events are removed to clean up the state.
     *
     * @param quizState The current quiz state.
     */
    public void processQuizEnd(QuizState quizState) {
        logger.info("Processing quiz end for QuizState ID: {}", quizState.getId());

        quizState.setActive(false);  // Set game as inactive

        // Clear temporary state-related data
        quizState.getQuizModifier().clearActiveQuizModifierEffects(); // Remove active modifier effects
        quizState.clearGameEvents();  // Clear game event history (note: this invalidates game save)

        saveQuizState(quizState);
        logger.debug("Successfully processed quiz end for QuizState ID: {}", quizState.getId());
    }

    /**
     * Determines and returns the next game event based on the current game state.
     * Ensures that no two special events (Shop/ModifierEffects) occur in direct succession.
     * Also enforces a minimum of 3 questions per segment before triggering a special event.
     *
     * @param quizState The current quiz state.
     * @return The next {@link GameEvent} instance.
     */
    public GameEvent getNextGameEvent(QuizState quizState) {
        Random random = new Random();

        // Prevent consecutive special events
        if (!quizState.getGameEvents().isEmpty()) {
            GameEvent lastEvent = quizState.getGameEvents().get(quizState.getGameEvents().size() - 1);
            if (lastEvent.getGameEventType() == GameEventType.SHOP ||
                    lastEvent.getGameEventType() == GameEventType.MODIFIER_EFFECTS) {
                logger.info("Last event was special ({}), forcing a QuestionGameEvent.", lastEvent.getGameEventType());
                return createQuestionGameEvent(quizState);
            }
        }

        // Ensure at least 3 questions are answered before allowing special events
        if (quizState.getAnsweredQuestionsInSegment() < 3) {
            logger.info("Less than 3 questions answered in current segment; returning QuestionGameEvent for QuizState ID: {}", quizState.getId());
            return createQuestionGameEvent(quizState);
        }

        // If both special events occurred in the segment, move to the next segment
        if (quizState.hasEventTypeOccurred(GameEventType.SHOP) &&
                quizState.hasEventTypeOccurred(GameEventType.MODIFIER_EFFECTS)) {
            moveToNextSegment(quizState);
        }

        // Determine event probabilities
        boolean canTriggerShop = !quizState.hasEventTypeOccurred(GameEventType.SHOP);
        boolean canTriggerModifier = !quizState.hasEventTypeOccurred(GameEventType.MODIFIER_EFFECTS);

        double shopChance = canTriggerShop ? 0.05 : 0.0;       // 5% chance for Shop event if not triggered
        double modifierChance = canTriggerModifier ? 0.10 : 0.0;  // 10% chance for Modifier event if not triggered

        double roll = random.nextDouble();

        if (canTriggerShop && roll < shopChance) {
            logger.info("Returning ShopGameEvent for QuizState ID: {}", quizState.getId());
            quizState.addEventTypeToSegment(GameEventType.SHOP);
            return createShopGameEvent(quizState);
        }

        if (canTriggerModifier && roll < (shopChance + modifierChance)) {
            logger.info("Returning ModifierEffectsGameEvent for QuizState ID: {}", quizState.getId());
            quizState.addEventTypeToSegment(GameEventType.MODIFIER_EFFECTS);
            return createModifierEffectsGameEvent(quizState);
        }

        // Default case: Return a question event
        logger.info("Returning next QuestionGameEvent for QuizState ID: {}", quizState.getId());
        return createQuestionGameEvent(quizState);
    }

    /**
     * Creates a new {@link QuestionGameEvent} based on the quiz state.
     * The difficulty and topic modifiers are considered when fetching the next question.
     *
     * @param quizState The current quiz state.
     * @return The newly created {@link QuestionGameEvent}.
     */
    public QuestionGameEvent createQuestionGameEvent(QuizState quizState) {
        logger.info("Creating QuestionGameEvent for QuizState ID: {}", quizState.getId());

        int difficultyModifier = quizState.getQuizModifier().getDifficultyModifier();
        Integer maxDifficultyModifier = quizState.getQuizModifier().getMaxDifficultyModifier();
        Integer minDifficultyModifier = quizState.getQuizModifier().getMinDifficultyModifier();
        String currentTopic = quizState.getQuizModifier().getTopicModifier();

        Question currentQuestion = null;

        // Select question based on modifiers
        if (maxDifficultyModifier != null) {
            currentQuestion = userQuestionService.getRandomQuestionExcludingCompletedWithMaxDifficultyLimit(
                    quizState.getCompletedQuestionIds(), maxDifficultyModifier, currentTopic);
        } else if (minDifficultyModifier != null) {
            currentQuestion = userQuestionService.getRandomQuestionExcludingCompletedWithMinDifficultyLimit(
                    quizState.getCompletedQuestionIds(), minDifficultyModifier, currentTopic);
        } else {
            currentQuestion = userQuestionService.getRandomQuestionExcludingCompleted(
                    quizState.getCompletedQuestionIds(), difficultyModifier, currentTopic);

            if (currentQuestion == null) {
                logger.info("Fallback: No question found for topic: {}, difficulty: {}. Using any difficulty.", currentTopic, difficultyModifier);
                currentQuestion = userQuestionService.getRandomQuestionExcludingCompleted(
                        quizState.getCompletedQuestionIds(), null, currentTopic);
            }
        }

        addQuestion(quizState, currentQuestion);
        QuestionGameEvent questionGameEvent = userQuestionService.createQuestionGameEvent(currentQuestion, quizState);
        quizState.addGameEvent(questionGameEvent);
        saveQuizState(quizState);

        logger.debug("Successfully created QuestionGameEvent for QuizState ID: {}", quizState.getId());
        return questionGameEvent;
    }



    /**
     * Creates a {@link ModifierEffectsGameEvent} for the given quiz state.
     * Randomly selects modifier effects and associates them with the event.
     *
     * @param quizState The current quiz state.
     * @return The created {@link ModifierEffectsGameEvent}.
     */
    public ModifierEffectsGameEvent createModifierEffectsGameEvent(QuizState quizState) {
        logger.info("Creating ModifierEffectsGameEvent for QuizState ID: {}", quizState.getId());
        List<QuizModifierEffectDto> randomQuizModifierEffects = userQuizModifierService.pickRandomModifierEffectDtos();

        List<UUID> effectUuids = randomQuizModifierEffects.stream()
                .map(id -> UUID.randomUUID())
                .collect(Collectors.toList());

        List<String> effectDescriptions = randomQuizModifierEffects.stream()
                .map(QuizModifierEffectDto::getDescription)
                .collect(Collectors.toList());

        List<String> effectIds = randomQuizModifierEffects.stream()
                .map(QuizModifierEffectDto::getIdString)
                .collect(Collectors.toList());

        List<Integer> effectTiers = randomQuizModifierEffects.stream()
                .map(QuizModifierEffectDto::getTier)
                .collect(Collectors.toList());

        List<Integer> effectDurations = randomQuizModifierEffects.stream()
                .map(QuizModifierEffectDto::getDuration)
                .collect(Collectors.toList());

        ModifierEffectsGameEvent modifierEffectsGameEvent = new ModifierEffectsGameEvent(
                quizState, effectUuids, effectIds, effectDescriptions, effectTiers, effectDurations
        );

        quizState.addGameEvent(modifierEffectsGameEvent);
        saveQuizState(quizState);
        logger.debug("Successfully created ModifierEffectsGameEvent for QuizState ID: {}", quizState.getId());

        return modifierEffectsGameEvent;
    }

    /**
     * Creates a {@link ShopGameEvent} for the given quiz state.
     * Randomly selects a set of jokers for the shop event.
     *
     * @param quizState The current quiz state.
     * @return The created {@link ShopGameEvent}.
     */
    public ShopGameEvent createShopGameEvent(QuizState quizState) {
        logger.info("Creating ShopGameEvent for QuizState ID: {}", quizState.getId());

        // Pick random Jokers
        List<JokerDto> chosenJokers = userJokerService.pickRandomJokerDtos();

        // Extract attributes from Joker DTOs
        List<UUID> jokerUuids = chosenJokers.stream()
                .map(JokerDto::getUuid)
                .collect(Collectors.toList());

        List<String> jokerIds = chosenJokers.stream()
                .map(JokerDto::getIdString)
                .collect(Collectors.toList());

        List<String> jokerNames = chosenJokers.stream()
                .map(JokerDto::getName)
                .collect(Collectors.toList());

        List<String> jokerDescriptions = chosenJokers.stream()
                .map(JokerDto::getDescription)
                .collect(Collectors.toList());

        List<Integer> jokerCosts = chosenJokers.stream()
                .map(JokerDto::getCost)
                .collect(Collectors.toList());

        List<Integer> jokerRarities = chosenJokers.stream()
                .map(dto -> dto.getRarity() != null ? dto.getRarity() : 1)
                .collect(Collectors.toList());

        List<Integer> jokerTiers = chosenJokers.stream()
                .map(dto -> dto.getTier() != null ? dto.getTier() : 1)
                .collect(Collectors.toList());

        // Create the ShopGameEvent
        ShopGameEvent shopGameEvent = new ShopGameEvent(
                quizState, jokerUuids, jokerIds, jokerNames, jokerDescriptions, jokerCosts, jokerRarities, jokerTiers
        );

        quizState.addGameEvent(shopGameEvent);
        saveQuizState(quizState);
        logger.debug("Successfully created ShopGameEvent for QuizState ID: {}", quizState.getId());

        return shopGameEvent;
    }

    /**
     * Validates whether the chosen modifier effect is valid for the last modifier event.
     *
     * @param quizState       The current quiz state.
     * @param chosenEffectUuid The UUID of the chosen modifier effect.
     * @return The last valid {@link ModifierEffectsGameEvent}.
     * @throws IllegalStateException If no valid event is found or the effect is invalid.
     */
    public ModifierEffectsGameEvent validateModifierChoiceEffectAgainstLastEvent(QuizState quizState, UUID chosenEffectUuid) {
        logger.info("Validating effect choice: {} for QuizState ID: {}", chosenEffectUuid, quizState.getId());

        if (quizState.getGameEvents().isEmpty()) {
            throw new IllegalStateException("No game events found for QuizState ID: " + quizState.getId());
        }

        GameEvent lastEvent = quizState.getGameEvents().getLast();

        if (!(lastEvent instanceof ModifierEffectsGameEvent modifierEffectsGameEvent)) {
            throw new IllegalStateException("The last game event is not a ModifierEffectsGameEvent");
        }

        if (modifierEffectsGameEvent.isConsumed()) {
            throw new IllegalStateException("The last ModifierEffectsGameEvent is already consumed");
        }

        if (!modifierEffectsGameEvent.getPresentedEffectUuids().contains(chosenEffectUuid)) {
            throw new IllegalArgumentException("Invalid effect choice: " + chosenEffectUuid);
        }

        logger.info("Effect choice validated for UUID: {}", chosenEffectUuid);
        return modifierEffectsGameEvent;
    }

    /**
     * Validates and applies a modifier effect chosen by the user.
     *
     * @param quizState       The current quiz state.
     * @param chosenEffectUuid The UUID of the chosen effect.
     * @return {@code true} if the effect was applied successfully; otherwise, {@code false}.
     */
    public boolean validateAndApplyModifierEffect(QuizState quizState, UUID chosenEffectUuid) {
        logger.info("Applying effect choice: {} for QuizState ID: {}", chosenEffectUuid, quizState.getId());

        ModifierEffectsGameEvent modifierEffectsGameEvent = validateModifierChoiceEffectAgainstLastEvent(quizState, chosenEffectUuid);

        List<UUID> presentedEffectUuids = modifierEffectsGameEvent.getPresentedEffectUuids();
        int effectIndex = presentedEffectUuids.indexOf(chosenEffectUuid);

        String effectId = modifierEffectsGameEvent.getPresentedEffectIdStrings().get(effectIndex);
        Integer tier = modifierEffectsGameEvent.getPresentedEffectTiers().get(effectIndex);
        Integer duration = modifierEffectsGameEvent.getPresentedEffectDurations().get(effectIndex);

        boolean effectApplied = userQuizModifierService.applyModifierEffectByIdString(quizState.getQuizModifier(), effectId, duration, tier);

        if (effectApplied) {
            modifierEffectsGameEvent.setConsumed(true);
            saveQuizState(quizState);
            logger.info("Successfully applied effect: {} and marked event as consumed", chosenEffectUuid);
            return true;
        }

        logger.error("Failed to instantiate and apply effect: {}", chosenEffectUuid);
        return false;
    }

    /**
     * Creates a {@link QuizSaveDto} from the given quiz state, including the latest game event.
     *
     * @param quizState The quiz state to save.
     * @return A {@link QuizSaveDto} containing the state and last game event.
     */
    public QuizSaveDto createQuizSaveDto(QuizState quizState) {
        logger.info("Creating QuizSaveDto from QuizState with ID: {}", quizState.getId());

        QuizStateDto quizStateDto = convertToDto(quizState);

        if (quizState.getGameEvents().isEmpty()) {
            logger.warn("No game events found for QuizState with ID: {}, returning QuizSaveDto as null", quizState.getId());
            return null;
        }

        GameEvent lastGameEvent = quizState.getGameEvents().getLast();
        GameEventDto lastGameEventDto = userGameEventService.convertToDto(lastGameEvent);

        logger.info("Successfully created QuizSaveDto for QuizState with ID: {}", quizState.getId());
        return new QuizSaveDto(quizStateDto, lastGameEventDto);
    }

}
