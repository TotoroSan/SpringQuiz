package com.example.quiz.integrationTest.service.admin;

import com.example.quiz.model.entity.QuizState;
import com.example.quiz.repository.QuizStateRepository;
import com.example.quiz.service.admin.AdminQuizStateService;
import com.example.quiz.service.user.UserQuizStateService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AdminQuizStateServiceIntegrationTest {

    @Autowired
    private AdminQuizStateService adminQuizStateService;

    @Autowired
    private QuizStateRepository quizStateRepository;

    @Autowired
    private UserQuizStateService userQuizStateService;

    private QuizState quizState;

    @BeforeEach
    void setup() {
        // Create a dummy QuizState for user id 1 and mark it as active
        quizState = new QuizState(1L);
        quizState.setActive(true);
        quizStateRepository.save(quizState);
    }

    @Test
    void testForceCompleteLatestQuizForUser() {
        QuizState completed = adminQuizStateService.forceCompleteLatestQuizForUser(1L);
        assertFalse(completed.isActive());
    }

    @Test
    void testCreateEmptyQuizState() {
        QuizState newState = adminQuizStateService.createEmptyQuizState(2L);
        assertNotNull(newState.getId());
        assertTrue(newState.isActive());
        assertEquals(2L, newState.getUserId());
    }

    @Test
    void testGetQuizState() {
        Optional<QuizState> fetchedOptional = userQuizStateService.getLatestQuizStateByUserId(quizState.getId());
        assertTrue(fetchedOptional.isPresent(), "QuizState should be present");
        QuizState fetched = fetchedOptional.get();
        assertEquals(quizState.getId(), fetched.getId());
    }

    @Test
    void testDeleteQuizState() {
        // First create a state that will be deleted
        QuizState toDelete = new QuizState(3L);
        quizStateRepository.save(toDelete);
        Long id = toDelete.getId();
        adminQuizStateService.deleteQuizState(id);
        Optional<QuizState> result = quizStateRepository.findById(id);
        assertTrue(result.isEmpty());
    }
}