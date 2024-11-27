package com.example.quiz.service.user;

import com.example.quiz.model.dto.*;
import com.example.quiz.model.entity.*;
import com.example.quiz.repository.QuizStateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserQuizStateService {
    private static final Logger logger = LoggerFactory.getLogger(UserQuizStateService.class);

    // responsible for actions that modify the state, like incrementing the question index.


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
    private UserGameEventService userGameEventService;


    // This should be transactional i think
    // Initialize quiz with questions fetched from the QuestionService
    // todo move the clearing of old states to a dedicated function (might run periodically)
    public QuizState startNewQuiz(Long userId) {
        logger.info("Starting new Quiz (creating new QuizState) for user: {}", userId);

        // We currently need this snippet to clear the last save game when starting a new game

        // Find all active quiz states for the user (only one quiz can be active at a time currently)
        List<QuizState> activeQuizStates = quizStateRepository.findAllByUserIdAndIsActiveTrue(userId);

        // Invalidate all active quiz states
        for (QuizState activeState : activeQuizStates) {
            logger.info("Clearing old QuizStates...");
            activeState.setActive(false);
            activeState.clearGameEvents(); // Clear associated GameEvents
            quizStateRepository.save(activeState);
        }

        // save and create new quizState
        QuizState quizState = new QuizState(userId);
        logger.debug("Successfully created new QuizState for user: {}", userId);

        return quizStateRepository.save(quizState);
    }

    public List<QuizState> getQuizStateHistory(Long userId) {
        return quizStateRepository.findAllByUserIdAndIsActiveFalse(userId);
    }


    // Method to get the latest quiz state by user ID
    public Optional<QuizState> getLatestQuizStateByUserId(Long userId) {
        return quizStateRepository.findFirstByUserIdOrderByIdDesc(userId);
    }

    // Method to get the latest quiz state by user ID
    public Optional<QuizState> getLatestActiveQuizStateByUserId(Long userId) {
        return quizStateRepository.findFirstByUserIdAndIsActiveIsTrueOrderByIdDesc(userId);
    }

    // Get all QuizStates by user ID
    public Optional<QuizState> getAllQuizStatesByUserId(Long userId) {
        return quizStateRepository.findByUserId(userId);
    }

    // Save changes to the QuizState
    public void saveQuizState(QuizState quizState) {
        quizStateRepository.save(quizState);
    }

    // Get the next question that hasn't been answered yet
    public Question getNextQuestion(QuizState quizState) {
        if (!hasMoreQuestions(quizState)) {
            return null;  // No more questions
        }
        Question currentQuestion = getCurrentQuestion(quizState);
        quizState.setCurrentQuestionIndex(quizState.getCurrentQuestionIndex() + 1);  // Move to the next question
        saveQuizState(quizState);
        return currentQuestion;
    }

    public void addQuestion(QuizState quizState, Question question) {
        logger.info("Adding question ", question.getId(), "to quizState");

        quizState.getAllQuestions().add(question);
        incrementCurrentQuestionIndex(quizState);
        saveQuizState(quizState);

        logger.debug("Added question ", question.getId(), "successfully");
    }

    public QuizStateDto convertToDto(QuizState quizState) {
        logger.info("Converting quizState to quizStateDto");
        // Convert to DTO to return to the user
        QuizStateDto quizStateDto = new QuizStateDto(
                quizState.getScore(),
                quizState.getCurrentRound(),
                quizState.getAllQuestions().isEmpty() ? null : quizState.getAllQuestions().get(quizState.getCurrentQuestionIndex()).getQuestionText(),
                userQuizModifierService.convertToDto(quizState.getQuizModifier()),
                quizState.isActive()); // convert quizModifier to dto as well in this process

        logger.debug("Successfully converted quizState to quizStateDto");
        return quizStateDto;
    }


    // Move to the next question
    public void incrementCurrentQuestionIndex(QuizState quizState) {
        quizState.setCurrentQuestionIndex(quizState.getCurrentQuestionIndex() + 1); // Move to the next question
    }

    // Increment the score by 1 (* multiplicator) // todo consolidate with overloaded function (?)
    public void incrementScore(QuizState quizState) {
        quizState.setScore(quizState.getScore() + (quizState.getQuizModifier().getScoreMultiplier() * 1));
        saveQuizState(quizState);
    }

    // Increment the score by increments ( * multiplicator)
    public void incrementScore(QuizState quizState, int increments) {
        quizState.setScore(quizState.getScore() + (quizState.getQuizModifier().getScoreMultiplier() * increments));
        saveQuizState(quizState);
    }



    // Increment the round
    public void incrementCurrentRound(QuizState quizState) {
        quizState.setCurrentRound(quizState.getCurrentRound() + 1);
    }

    // Mark a question as completed
    public void markQuestionAsCompleted(QuizState quizState, Long questionId) {
        quizState.getCompletedQuestionIds().add(questionId);
    }

    // Get the current question
    public Question getCurrentQuestion(QuizState quizState) {
        logger.info("Retreiving current question from quizState");
        if (quizState.getCurrentQuestionIndex() < quizState.getAllQuestions().size()) {
            return quizState.getAllQuestions().get(quizState.getCurrentQuestionIndex());
        }

        logger.debug("Retreival of current question from quizState failed");
        return null;  // No more questions
    }

    // Check if a question is completed
    public boolean isCompleted(QuizState quizState, Long questionId) {
        return quizState.getCompletedQuestionIds().contains(questionId);
    }

    // Check if quiz has more questions
    public boolean hasMoreQuestions(QuizState quizState) {
        return quizState.getCurrentQuestionIndex() < quizState.getAllQuestions().size();
    }

    // Move to the next segment (i.e. reset question count in segment)
    public void moveToNextSegment(QuizState quizState) {
        quizState.setCurrentSegment(quizState.getCurrentSegment() + 1);
        quizState.setAnsweredQuestionsInSegment(1);
        saveQuizState(quizState);
    }

    // Todo methods like this should be moved to the model class itself
    public void incrementAnsweredQuestionsInSegment(QuizState quizState) {
        quizState.setAnsweredQuestionsInSegment(quizState.getAnsweredQuestionsInSegment() + 1);
    }

    // Update the game state after a correct answer was submitted
    public void processCorrectAnswerSubmission(QuizState quizState) {
        logger.info("Processing correct answer submission for", quizState);

        // update quizState
        markQuestionAsCompleted(quizState, getCurrentQuestion(quizState).getId());
        incrementScore(quizState, getCurrentQuestion(quizState).getDifficulty()); // todo change here if we want every question to have same score (currently score of a question = difficulty)
        incrementCurrentRound(quizState);
        incrementAnsweredQuestionsInSegment(quizState);
        // update ActiveQuizModifierEffects
        userQuizModifierService.processActiveQuizModifierEffectsForNewRound(quizState.getQuizModifier());
        // Persist the updated quiz state
        saveQuizState(quizState);

        logger.debug("Successfully processed correct answer submission for", quizState);
    }


    // Update the game state after a correct answer was submitted
    public void processQuizEnd(QuizState quizState) {
        logger.info("Processing quiz end for QuizState", quizState);

        quizState.setActive(false);  // set game as inactive on wrong answer

        // clearing out part of the quizstate data that is should not be permanent as of now
        quizState.getQuizModifier().clearActiveQuizModifierEffects(); // clear active modifier effects
        quizState.clearGameEvents();  // clear game event history TODO careful this automatically invalidates/corrupts the save of the game (=> currently no loading of ended games)

        saveQuizState(quizState);

        logger.debug("Successfully processed quiz end");
    }

    // Update the game state after a correct answer was submitted
    public void processIncorrectAnswerSubmission(QuizState quizState) {
        logger.info("Processing incorrect answer for QuizState", quizState);

        userQuizModifierService.decrementLifeCounter(quizState.getQuizModifier());

        if (quizState.getQuizModifier().getLifeCounter() <= 0) {
            logger.info("No lifes left, initiating quiz end");
            processQuizEnd(quizState);
        }

        saveQuizState(quizState);
        logger.debug("Successfully processed incorrect answer");
    }


    // cann return either subtype
    public GameEvent getNextGameEvent(QuizState quizState) {
        // Check if the current round is divisible by 5 to provide modifier effects
        // TODO 5 is arbitrary value for testing.


        if (quizState.getAnsweredQuestionsInSegment() % 5 == 0) {
            logger.info("Returning random modifier effects for QuizState ID: {}", quizState.getId());


            List<QuizModifierEffectDto> randomQuizModifierEffects = userQuizModifierService.pickRandomModifierEffectDtos();

            // Extract IDs from the list of QuizModifierEffectDto
            List<String> effectIds = randomQuizModifierEffects.stream()
                    .map(QuizModifierEffectDto::getId)
                    .collect(Collectors.toList());

            ModifierEffectsGameEvent modifierEffectsGameEvent = new ModifierEffectsGameEvent(quizState, effectIds);

            quizState.addGameEvent(modifierEffectsGameEvent);
            saveQuizState(quizState);

            logger.debug("Successfully returned random modifier effects game event");
            return modifierEffectsGameEvent;
        } else {
            logger.info("Returning next question for QuizState ID: {}", quizState.getId());

            // Return the next question with the given difficulty
            int difficultyModifier = quizState.getQuizModifier().getDifficultyModifier();
            Integer maxDifficultyModifier = quizState.getQuizModifier().getMaxDifficultyModifier();
            Integer minDifficultyModifier = quizState.getQuizModifier().getMinDifficultyModifier();
            // get topic modifier, if topic is set pass topic
            String currentTopic = quizState.getQuizModifier().getTopicModifier();

            Question currentQuestion = null;

            // if there is a max difficulty modifier use it, else if there is a min modifier use this and if there is no min or max use the default difficulty modifier
            if (maxDifficultyModifier != null) {
                currentQuestion = userQuestionService.getRandomQuestionExcludingCompletedWithMaxDifficultyLimit(quizState.getCompletedQuestionIds(), maxDifficultyModifier, currentTopic);
            } else if (minDifficultyModifier != null) {
                currentQuestion = userQuestionService.getRandomQuestionExcludingCompletedWithMinDifficultyLimit(quizState.getCompletedQuestionIds(), minDifficultyModifier, currentTopic);
            } else {
                // todo this if solution is a temporary workaround
                currentQuestion = userQuestionService.getRandomQuestionExcludingCompleted(quizState.getCompletedQuestionIds(), difficultyModifier, currentTopic);
                // if we dont find question for given difficulty, use any difficulty
                if (currentQuestion == null) {
                    logger.info("Used fallback to find question with any difficulty, because no question for topic: ", currentTopic, " with difficulty: ", difficultyModifier, " found.");
                    currentQuestion = userQuestionService.getRandomQuestionExcludingCompleted(quizState.getCompletedQuestionIds(), null, currentTopic);
                }
            }
            addQuestion(quizState, currentQuestion); // todo check if this needs to stay here or if we maybe consolidate with processnextgamestep or sth
            QuestionGameEvent questionGameEvent = userQuestionService.createQuestionGameEvent(currentQuestion, quizState);
            quizState.addGameEvent(questionGameEvent);
            saveQuizState(quizState);

            logger.debug("Successfully returned question game event");
            return questionGameEvent;
        }
    }


    public QuizSaveDto createQuizSaveDto(QuizState quizState) {
        logger.info("Creating QuizSaveDto from QuizState with ID: {}", quizState.getId());

        QuizStateDto quizStateDto = convertToDto(quizState);

        // Safely retrieve the last game event
        if (quizState.getGameEvents().isEmpty()) {
            logger.warn("No game events found for QuizState with ID: {}", quizState.getId(), "returning QuizSaveDto as null");
            // todo check when question is added to gameevents
            return null; // save state without GameEvents is invalid (saved without having a question)
        }

        GameEvent lastGameEvent = quizState.getGameEvents().getLast();
        GameEventDto lastGameEventDto = userGameEventService.convertToDto(lastGameEvent);

        logger.info("Successfully created QuizSaveDto for QuizState with ID: {}", quizState.getId());
        return new QuizSaveDto(quizStateDto, lastGameEventDto);
    }


}
