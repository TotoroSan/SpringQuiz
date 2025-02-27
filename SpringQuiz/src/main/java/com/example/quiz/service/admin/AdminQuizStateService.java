package com.example.quiz.service.admin;

import com.example.quiz.model.entity.QuizState;
import com.example.quiz.repository.QuizStateRepository;
import com.example.quiz.service.user.UserQuizStateService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


/**
 * Service for administrative operations on quiz states.
 * This service provides a thin wrapper around repository operations,
 * adding admin permission checks and logging.
 */
@Service
public class AdminQuizStateService {
    private static final Logger logger = LoggerFactory.getLogger(AdminQuizStateService.class);

    private final QuizStateRepository quizStateRepository;
    private final UserQuizStateService userQuizStateService;

    @Autowired
    public AdminQuizStateService(QuizStateRepository quizStateRepository, UserQuizStateService userQuizStateService) {
        this.quizStateRepository = quizStateRepository;
        this.userQuizStateService = userQuizStateService;
    }





    /**
     * Deletes a quiz state, with admin permission check.
     *
     * @param quizStateId The ID of the quiz state to delete
     */
    @Transactional
    public void deleteQuizState(Long quizStateId) {
        logger.info("Admin deleting QuizState with ID: {}", quizStateId);

        quizStateRepository.deleteById(quizStateId);
    }

    /**
     * Forces the latest quiz state for a user to be marked as complete.
     *
     * @param userId The ID of the user whose quiz state should be completed
     * @return The updated quiz state
     * @throws EntityNotFoundException if no active quiz state is found for the user
     */
    @Transactional
    public QuizState forceCompleteLatestQuizForUser(Long userId) {
        logger.info("Admin forcing completion of latest QuizState for user ID: {}", userId);

        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestQuizStateByUserId(userId);
        if (optionalQuizState.isEmpty()) {
            throw new EntityNotFoundException("No quiz state found for user ID: " + userId);
        }

        QuizState quizState = optionalQuizState.get();
        quizState.setActive(false);
        return quizStateRepository.save(quizState);
    }

    /**
     * Creates a new empty quiz state for a user, with admin permission check.
     *
     * @param userId The ID of the user
     * @return The newly created quiz state
     */
    @Transactional
    public QuizState createEmptyQuizState(Long userId) {
        logger.info("Admin creating empty QuizState for user ID: {}", userId);

        QuizState quizState = new QuizState(userId);
        return quizStateRepository.save(quizState);
    }


}