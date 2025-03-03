package com.example.quiz.repository;

import com.example.quiz.model.entity.GameEvent;
import com.example.quiz.model.entity.QuestionGameEvent; // Corrected class name
import com.example.quiz.model.enums.GameEventType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class GameEventRepositoryIntegrationTest {

    @Autowired
    private GameEventRepository gameEventRepository;

    @Test
    public void testSaveAndFindGameEvent() {
        // Create a concrete implementation of GameEvent
        QuestionGameEvent gameEvent = new QuestionGameEvent(); // Corrected class name
        gameEvent.setGameEventType(GameEventType.QUESTION);
        // Set additional fields specific to QuestionGameEvent
        gameEvent.setQuestionId(1L);

        // Save the entity
        GameEvent savedEvent = gameEventRepository.save(gameEvent);
        assertNotNull(savedEvent.getId());

        // Retrieve the entity by ID
        GameEvent retrievedEvent = gameEventRepository.findById(savedEvent.getId()).orElse(null);
        assertNotNull(retrievedEvent);
        assertTrue(retrievedEvent instanceof QuestionGameEvent);
        assertEquals(GameEventType.QUESTION, retrievedEvent.getGameEventType());

        QuestionGameEvent typedEvent = (QuestionGameEvent) retrievedEvent;
        assertEquals(1L, typedEvent.getQuestionId());
    }

    @Test
    public void testUpdateGameEvent() {
        // Create and save a concrete game event
        QuestionGameEvent gameEvent = new QuestionGameEvent();
        gameEvent.setGameEventType(GameEventType.QUESTION);
        gameEvent.setQuestionId(2L);

        GameEvent savedEvent = gameEventRepository.save(gameEvent);

        // Update the event
        savedEvent.setGameEventType(GameEventType.QUESTION);
        gameEventRepository.save(savedEvent);

        // Verify update
        GameEvent updatedEvent = gameEventRepository.findById(savedEvent.getId()).orElse(null);
        assertNotNull(updatedEvent);
        assertEquals(GameEventType.QUESTION, updatedEvent.getGameEventType());
    }

    @Test
    public void testDeleteGameEvent() {
        // Create and save a concrete game event
        QuestionGameEvent gameEvent = new QuestionGameEvent();
        gameEvent.setGameEventType(GameEventType.QUESTION);
        gameEvent.setQuestionId(3L);

        GameEvent savedEvent = gameEventRepository.save(gameEvent);
        Long id = savedEvent.getId();

        // Delete the event
        gameEventRepository.delete(savedEvent);

        // Verify deletion
        assertFalse(gameEventRepository.existsById(id));
    }
}