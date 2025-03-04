// File: `src/test/java/com/example/quiz/repository/GameEventRepositoryTest.java`
package com.example.quiz.unitTest.repository;

import com.example.quiz.model.entity.GameEvent;
import com.example.quiz.model.entity.QuestionGameEvent;
import com.example.quiz.model.enums.GameEventType;
import com.example.quiz.repository.GameEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class GameEventRepositoryTest {

    @Autowired
    private GameEventRepository gameEventRepository;

    @Test
    public void testSaveAndFindGameEvent() {
        GameEvent event = new QuestionGameEvent();
        event.setGameEventType(GameEventType.QUESTION);
        GameEvent saved = gameEventRepository.save(event);
        assertNotNull(saved.getId());

        GameEvent found = gameEventRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals(GameEventType.QUESTION, found.getGameEventType());
    }
}