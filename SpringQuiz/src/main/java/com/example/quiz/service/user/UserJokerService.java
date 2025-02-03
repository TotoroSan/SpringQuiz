package com.example.quiz.service.user;

import com.example.quiz.model.dto.JokerDto;
import com.example.quiz.model.entity.*;
import com.example.quiz.model.entity.Joker.*;
import com.example.quiz.repository.QuizStateRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserJokerService {
    private static final Logger logger = LoggerFactory.getLogger(UserJokerService.class);

    @Autowired
    private QuizStateRepository quizStateRepository;

    /**
     * Picks a list of random Joker DTOs using weighted probabilities.
     * Up to 3 jokers are picked from the metadata registry.
     *
     * @return A list of JokerDto objects.
     */
    public List<JokerDto> pickRandomJokerDtos() {
        logger.info("Picking random jokers with weighted probabilities");

        // Fetch all Joker metadata from the registry
        List<JokerMetaData> allJokers = new ArrayList<>(JokerFactory.getJokerMetadataRegistry().values());

        List<JokerDto> selectedJokers = new ArrayList<>();
        Random random = new Random();

        // Pick up to 3 jokers via weighted picking
        for (int i = 0; i < 3 && !allJokers.isEmpty(); i++) {
            // Calculate total weight
            int totalWeight = allJokers.stream()
                    .mapToInt(JokerMetaData::getRarityWeight)
                    .sum();

            // Pick a random value in the range [0, totalWeight)
            int randomValue = random.nextInt(totalWeight);
            int cumulativeWeight = 0;
            JokerMetaData chosenJoker = null;

            // Iterate through metadata to find the selected joker
            for (JokerMetaData jokerMetaData : allJokers) {
                cumulativeWeight += jokerMetaData.getRarityWeight();
                if (randomValue < cumulativeWeight) {
                    chosenJoker = jokerMetaData;
                    logger.info("Chosen Joker: {}", chosenJoker.getIdString());
                    break;
                }
            }

            if (chosenJoker != null) {
                // Roll a tier for the joker (if not provided)
                int rolledTier = JokerFactory.rollTier();
                // Create a Joker instance using the factory
                Joker joker = JokerFactory.createJoker(chosenJoker.getIdString(), rolledTier);
                // Convert to DTO and add to the selection
                selectedJokers.add(convertToDto(joker));
                // Remove the chosen joker to avoid duplicates in this selection
                allJokers.remove(chosenJoker);
            }
        }

        logger.debug("Selected jokers: {}", selectedJokers);
        return selectedJokers;
    }

    /**
     * Purchases a Joker by deducting its cost from the QuizState's coin balance
     * and adding a new Joker instance (created via the factory) to the active jokers list.
     * This method ensures the entire purchase is performed atomically.
     *
     * @param quizState The current quiz state.
     * @param jokerId   The Joker identifier.
     * @param tier      The desired joker tier (if null, a tier will be rolled).
     * @return True if the Joker was successfully purchased and added; false otherwise.
     */
    @Transactional
    public boolean purchaseJoker(QuizState quizState, String jokerId, Integer tier) {
        logger.info("Purchasing Joker with id: {} and tier: {}", jokerId, tier);

        // Retrieve metadata for the specified Joker from the factory registry.
        JokerMetaData metaData = JokerFactory.getJokerMetadataRegistry().get(jokerId);
        // Retrieve QuizModifier to deduct cash
        QuizModifier quizModifier = quizState.getQuizModifier();

        if (metaData == null) {
            logger.error("No metadata found for Joker id: {}", jokerId);
            return false;
        }

        // Check if the QuizState has enough coins for the purchase.
        int cost = metaData.getCost();
        if (quizModifier.getCash() < cost) {
            logger.warn("Insufficient funds: required {} coins, available {} coins", cost, quizModifier.getCash());
            return false;
        }

        // Deduct the cost from the QuizState's coin balance.
        quizModifier.setCash(quizModifier.getCash() - cost);

        // Instantiate the Joker using the factory.
        // Note: The rarity is drawn from the registry, so it is not passed as an argument.
        Joker joker = JokerFactory.createJoker(jokerId, tier);
        if (joker != null) {
            // Add the new Joker to the active jokers list.
            quizState.getActiveJokers().put(joker.getId(), joker);
            quizStateRepository.save(quizState);
            logger.info("Joker {} purchased and added to active jokers", joker.getIdString());
            return true;
        }

        logger.warn("Failed to instantiate Joker for id: {}", jokerId);
        return false;
    }

    /**
     * Uses (applies) a Joker that was previously purchased.
     * The frontend should send the persistent object id of the Joker.
     * This method locates the Joker in the active list and applies its effect (placeholder),
     * then removes it from the list.
     *
     * @param quizState     The current quiz state.
     * @param jokerObjectId The persistent object id of the Joker to be used.
     * @return True if the Joker was successfully applied and removed; false otherwise.
     */

    // appliance of jokers is different to modifiereffects - since the joker effects are more complex
    // each joker has its own effect function in this service instead of the joker class itself
    public boolean useJoker(QuizState quizState, Long jokerObjectId) {
        logger.info("Attempting to use Joker with object id: {}", jokerObjectId);

        HashMap<Long, Joker> activeJokers = quizState.getActiveJokers();
        Joker jokerToUse = activeJokers.get(jokerObjectId);

        if (jokerToUse == null) {
            logger.warn("No active Joker found with object id: {}", jokerObjectId);
            return false;
        }

        // If the Joker has 0 uses, we skip applying it
        if (jokerToUse.getUses() == null || jokerToUse.getUses() <= 0) {
            logger.warn("Joker {} has no uses left. Cannot apply.", jokerToUse.getIdString());
            return false;
        }

        // Determine which Joker it is by checking the subclass
        if (jokerToUse instanceof FiftyFiftyJoker) {
            applyFiftyFiftyJoker(quizState, jokerToUse);
        } else if (jokerToUse instanceof SkipQuestionJoker) {
            applySkipQuestionJoker(quizState, jokerToUse);
        } else if (jokerToUse instanceof TwentyFiveSeventyFiveJoker) {
            applyTwentyFiveSeventyFiveJoker(quizState, jokerToUse);
        } else {
            logger.warn("Unknown Joker type: {}", jokerToUse.getClass().getSimpleName());
        }

        // Decrement or remove from inventory
        int currentUses = jokerToUse.getUses();
        if (currentUses > 1) {
            jokerToUse.setUses(currentUses - 1);
            logger.info("Reduced uses of Joker {}. New uses: {}", jokerToUse.getIdString(), jokerToUse.getUses());
        } else {
            activeJokers.remove(jokerObjectId);
            logger.info("Joker {} removed from active jokers", jokerToUse.getIdString());
        }

        quizStateRepository.save(quizState);
        return true;
    }


    /**
     * Eliminates two wrong answers from the current question.
     */
    public void applyFiftyFiftyJoker(QuizState quizState, Joker joker) {
        logger.info("Applying 50/50 effect from Joker {}", joker.getIdString());

        GameEvent latestEvent = quizState.getLatestGameEvent();
        if (latestEvent == null) {
            logger.warn("No latest game event available, cannot apply 50/50.");
            return;
        }

        if (!(latestEvent instanceof QuestionGameEvent questionGameEvent)) {
            logger.warn("Latest game event is not a QuestionGameEvent: {}", latestEvent.getClass().getSimpleName());
            return;
        }

        // Load the Question from quizState
        Question question = quizState.getCurrentQuestion();
        if (question == null) {
            logger.warn("No Question found for id {}. Cannot apply 50/50.");
            return;
        }

        // Identify the correct answer. If you have only one correct answer, you can do:
        Long correctAnswerId = question.getCorrectAnswer().getId();
        // If multiple, adapt accordingly

        // Now get all answer IDs from the questionEvent's shuffledAnswers
        List<Answer> shuffledAnswers = questionGameEvent.getShuffledAnswers();
        if (shuffledAnswers == null || shuffledAnswers.isEmpty()) {
            logger.warn("No shuffled answers present in QuestionGameEvent. Cannot apply 50/50.");
            return;
        }

        // Identify wrong answers as a list of Answer objects
        List<Answer> wrongAnswers = shuffledAnswers.stream()
                .filter(answer -> !answer.getId().equals(correctAnswerId))
                .collect(Collectors.toList());

        if (wrongAnswers.size() < 2) {
            logger.warn("Not enough wrong answers to apply 50/50. Wrong answers: {}", wrongAnswers.size());
            return;
        }

        // Shuffle and pick 2
        Collections.shuffle(wrongAnswers);
        List<Answer> answersToEliminate = wrongAnswers.subList(0, 2);

        // Add their IDs to eliminatedAnswerIds
        answersToEliminate.forEach(a -> questionGameEvent.getEliminatedAnswerIds().add(a.getId()));


        logger.info("Eliminated answer IDs: {}", answersToEliminate);

        quizStateRepository.save(quizState);
    }

    /**
     * Eliminates one wrong answer from the current question
     */
    public void applyTwentyFiveSeventyFiveJoker(QuizState quizState, Joker joker) {
        logger.info("Applying 25/75 effect from Joker {}", joker.getIdString());

        GameEvent latestEvent = quizState.getLatestGameEvent();
        if (latestEvent == null) {
            logger.warn("No latest game event available, cannot apply 25/75.");
            return;
        }

        if (!(latestEvent instanceof QuestionGameEvent questionEvent)) {
            logger.warn("Latest game event is not a QuestionGameEvent. Type: {}",
                    latestEvent.getClass().getSimpleName());
            return;
        }

        // If you store the correct answer or can look it up similarly
        List<Answer> shuffledAnswers = questionEvent.getShuffledAnswers();
        if (shuffledAnswers == null || shuffledAnswers.isEmpty()) {
            logger.warn("No shuffled answers present, cannot apply 25/75.");
            return;
        }

        // Load the Question from quizState
        Question question = quizState.getCurrentQuestion();
        if (question == null) {
            logger.warn("No Question found for id {}. Cannot apply 50/50.");
            return;
        }

        // Identify the correct answer. If you have only one correct answer, you can do:
        Long correctAnswerId = question.getCorrectAnswer().getId();

        List<Answer> wrongAnswers = shuffledAnswers.stream()
                .filter(a -> !a.getId().equals(correctAnswerId))
                .collect(Collectors.toList());

        if (wrongAnswers.size() < 1) {
            logger.warn("No wrong answers to eliminate for 25/75.");
            return;
        }

        // Randomly pick 1 wrong answer to remove
        Collections.shuffle(wrongAnswers);
        Answer answerToEliminate = wrongAnswers.get(0);

        // Add it to eliminatedAnswerIds
        questionEvent.getEliminatedAnswerIds().add(answerToEliminate.getId());

        logger.info("Eliminated answer ID: {}", answerToEliminate.getId());

        quizStateRepository.save(quizState);
    }

    /**
     * Marks the current question as skipped and moves on to the next question event.
     */
    public void applySkipQuestionJoker(QuizState quizState, Joker joker) {
        logger.info("Applying Skip Question effect from Joker {}", joker.getIdString());

        GameEvent latestEvent = quizState.getLatestGameEvent();
        if (latestEvent == null) {
            logger.warn("No latest game event available, cannot skip question.");
            return;
        }

        // Ensure it's a QuestionGameEvent
        if (!(latestEvent instanceof QuestionGameEvent questionGameEvent)) {
            logger.warn("Latest game event is not a QuestionGameEvent. Type: {}",
                    latestEvent.getClass().getSimpleName());
            return;
        }

        // Mark the current question as skipped
        questionGameEvent.setSkipUsed(true);


        // Save changes (both skipUsed on current event and the new index or new event)
        quizStateRepository.save(quizState);

        logger.info("QuestionGameEvent {} was skipped; advanced to the next event: {}");
    }

    /**
     * Converts a Joker entity to a JokerDto.
     *
     * @param joker The Joker entity.
     * @return The corresponding JokerDto.
     */
    public JokerDto convertToDto(Joker joker) {
        // Assuming JokerDto has a constructor:
        // (Long id, String idString, String name, String description, int cost, Integer uses, Integer tier, int rarity)
        return new JokerDto(
                joker.getId(),
                joker.getIdString(),
                joker.getName(),
                joker.getDescription(),
                joker.getCost(),
                joker.getUses(),
                joker.getRarity(),
                joker.getTier()
        );
    }

}
