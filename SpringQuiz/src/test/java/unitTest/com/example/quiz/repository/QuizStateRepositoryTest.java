package unitTest.com.example.quiz.repository;

import com.example.quiz.model.entity.QuizState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class QuizStateRepositoryTest {

    @Autowired
    private QuizStateRepository quizStateRepository;

    @Test
    public void testSaveAndFindQuizState() {
        QuizState state = new QuizState();
        state.setUserId(1L);
        state.setActive(true);
        QuizState saved = quizStateRepository.save(state);
        assertNotNull(saved.getId());

        QuizState found = quizStateRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals(1L, found.getUserId());
        assertTrue(found.isActive());
    }
}