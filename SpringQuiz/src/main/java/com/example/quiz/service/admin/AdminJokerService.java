package com.example.quiz.service.admin;

import com.example.quiz.model.entity.Joker.Joker;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.service.user.UserJokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminJokerService {
    private static final Logger logger = LoggerFactory.getLogger(AdminJokerService.class);

    private final UserJokerService userJokerService;

    @Autowired
    public AdminJokerService(UserJokerService userJokerService) {
        this.userJokerService = userJokerService;
    }

    /**
     * Applies a joker effect for testing purposes on a quiz state.
     * This method creates a temporary joker using JokerFactory and applies its effect.
     *
     * @param quizState    The quiz state to apply the joker effect to
     * @param jokerIdString The identifier string of the joker type to test
     * @param tier         The tier of the joker (determines power level)
     * @return true if the joker effect was successfully applied, false otherwise
     */
    public boolean applyJokerForTesting(QuizState quizState, String jokerIdString, Integer tier) {
        logger.info("Testing joker effect: {} with tier: {}", jokerIdString, tier);

        // Create a temporary joker instance using the factory
        Joker testJoker = com.example.quiz.model.entity.Joker.JokerFactory.createJoker(
                quizState, jokerIdString, tier);

        if (testJoker == null) {
            logger.warn("Failed to create test joker with ID: {}", jokerIdString);
            return false;
        }

        // Apply appropriate joker effect based on the joker type
        boolean result;
        switch (jokerIdString) {
            case "FIFTY_FIFTY":
                result = userJokerService.applyFiftyFiftyJoker(quizState, testJoker);
                break;
            case "TWENTYFIVE_SEVENTYFIVE":
                result = userJokerService.applyTwentyFiveSeventyFiveJoker(quizState, testJoker);
                break;
            case "SKIP_QUESTION":
                result = userJokerService.applySkipQuestionJoker(quizState, testJoker);
                break;
            default:
                logger.warn("Unknown joker type: {}", jokerIdString);
                return false;
        }

        logger.info("Joker effect application result: {}", result);
        return result;
    }
}