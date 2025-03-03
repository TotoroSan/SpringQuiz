package com.example.quiz.integrationTest.service.admin;

import com.example.quiz.model.dto.JokerDto;
import com.example.quiz.model.entity.Joker.Joker;
import com.example.quiz.model.entity.Joker.JokerFactory;
import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.repository.QuizStateRepository;
import com.example.quiz.service.admin.AdminGameEventService;
import com.example.quiz.service.user.UserGameEventService;
import com.example.quiz.service.user.UserJokerService;
import com.example.quiz.service.user.UserQuizStateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminGameEventServiceIntegrationTest {

    @Autowired
    private AdminGameEventService adminGameEventService;

    @Autowired
    private QuizStateRepository quizStateRepository;

    @Autowired
    private UserQuizStateService userQuizStateService;

    @Autowired
    private UserJokerService userJokerService;

    private QuizState quizState;

    @BeforeEach
    void setup() {
        // Create a dummy QuizState for user id 1
        quizState = new QuizState(1L);
        quizState.setActive(true);
        quizStateRepository.save(quizState);
    }

    @Test
    void testTriggerShopEvent() {
        // Should not throw errors and shop event is triggered
        adminGameEventService.triggerShopEvent(quizState);
    }

    @Test
    void testTriggerQuestionEvent() {
        adminGameEventService.triggerQuestionEvent(quizState, 1);
    }

    @Test
    void testTriggerModifierEvent() {
        adminGameEventService.triggerModifierEvent(quizState);
    }

    @Test
    void testAddAndRemoveJoker() {
        // Set up the input DTO with the required data.
        // The service will use the factory internally to create the joker.
        Joker input = JokerFactory.createJoker(quizState, "FIFTY_FIFTY", 1);
        JokerDto inputDto = userJokerService.convertToDto(input);
        inputDto.setIdString("FIFTY_FIFTY");
        inputDto.setTier(1);

        // Call the service and receive a DTO created from the factory-created joker.
        JokerDto returnedDto = adminGameEventService.addJokerToQuizState(quizState, inputDto);
        assertNotNull(returnedDto);

        // Verify that a joker was added to the owned jokers map.
        assertFalse(quizState.getOwnedJokers().isEmpty());

        // Remove the joker and verify removal.
        UUID jokerId = quizState.getOwnedJokers().keySet().iterator().next();
        adminGameEventService.removeJokerFromQuizState(quizState, jokerId);
        assertTrue(quizState.getOwnedJokers().isEmpty());
    }

    @Test
    void testModifyQuizModifier() {
        // Assuming quizState has a QuizModifier already
        QuizModifier initialModifier = quizState.getQuizModifier();
        initialModifier.setCash(100);
        initialModifier.setLifeCounter(3);
        initialModifier.setScoreMultiplier(1.0);
        quizState.setQuizModifier(initialModifier);
        quizStateRepository.save(quizState);

        // Create updated modifier with new properties
        QuizModifier updated = quizState.getQuizModifier();
        updated.setCash(200);
        updated.setLifeCounter(5);
        updated.setScoreMultiplier(2.0);

        QuizModifier result = adminGameEventService.modifyQuizModifier(quizState, updated);
        assertEquals(200, result.getCash());
        assertEquals(5, result.getLifeCounter());
        assertEquals(2.0, result.getScoreMultiplier(), 0.001);
    }

    @Test
    void testTestJokerEffect() {
        String result = adminGameEventService.testJokerEffect(quizState, "FIFTY_FIFTY");
        assertTrue(result.contains("successfully") || result.contains("Failed"));
    }
}