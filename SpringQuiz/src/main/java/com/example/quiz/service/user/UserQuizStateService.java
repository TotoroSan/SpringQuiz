package com.example.quiz.service.user;

import com.example.quiz.model.dto.QuizStateDto;
import com.example.quiz.model.entity.Question;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.repository.QuizStateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    // Initialize quiz with questions fetched from the QuestionService
    public QuizState startNewQuiz(Long userId) {
        QuizState quizState = new QuizState(userId);
        return quizStateRepository.save(quizState);
    }

    // Method to get the latest quiz state by user ID
    public Optional<QuizState> getLatestQuizStateByUserId(Long userId) {
        return quizStateRepository.findFirstByUserIdOrderByIdDesc(userId);
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
                userQuizModifierService.convertToDto(quizState.getQuizModifier())); // convert quizModifier to dto as well in this process

        logger.debug("Successfully converted quizState to quizStateDto");
        return quizStateDto;
    }


    // Move to the next question
    public void incrementCurrentQuestionIndex(QuizState quizState) {
        quizState.setCurrentQuestionIndex(quizState.getCurrentQuestionIndex() + 1); // Move to the next question
    }

    // Increment the score by 1 (* multiplicator)
    public void incrementScore(QuizState quizState) {
        quizState.setScore(quizState.getScore() + (quizState.getQuizModifier().getScoreMultiplier() * 1));
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
        incrementScore(quizState);
        incrementCurrentRound(quizState);
        incrementAnsweredQuestionsInSegment(quizState);
        // update ActiveQuizModifierEffects
        userQuizModifierService.processActiveQuizModifierEffectsForNewRound(quizState.getQuizModifier());
        // Persist the updated quiz state
        saveQuizState(quizState);

        logger.debug("Successfully processed correct answer submission for", quizState);
    }

    // Update the game state after a incorrect answer was submitted
    // todo add logic here if we want to continue the quiz after wrong answer submission. for the now the quiz ends on incorrect submission
    public void processIncorrectAnswerSubmission(QuizState quizState) {

    }

    // Update the game state after a correct answer was submitted
    public void processQuizEnd(QuizState quizState) {
        logger.info("Processing quiz end for QuizState", quizState);

        quizState.setActive(false);  // set game as inactive on wrong answer
        quizState.getQuizModifier().getActiveQuizModifierEffects().clear(); // clear active effects when the game ends
        saveQuizState(quizState);

        logger.debug("Successfully processed quiz end");
    }
}
