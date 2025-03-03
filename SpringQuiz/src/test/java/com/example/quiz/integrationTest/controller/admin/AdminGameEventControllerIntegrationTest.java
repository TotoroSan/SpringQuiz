package com.example.quiz.integrationTest.controller.admin;

import com.example.quiz.model.dto.GameEventDto;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.model.entity.User;
import com.example.quiz.repository.QuizStateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminGameEventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QuizStateRepository quizStateRepository;

    private QuizState dummyQuizState;

    @BeforeEach
    void setup() {
        // create and save a dummy QuizState associated with user id 1
        dummyQuizState = new QuizState(1L);
        dummyQuizState.setActive(true);
        quizStateRepository.save(dummyQuizState);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testForceQuestionEvent() throws Exception {
        // Call the endpoint; the controller will look up an active QuizState for user id = 1
        // For integration tests, assume the dummy quiz state's owner id is 1.
        mockMvc.perform(post("/api/admin/quiz/event/question")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testForceShopEvent() throws Exception {
        mockMvc.perform(post("/api/admin/quiz/event/shop")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testForceModifierEvent() throws Exception {
        mockMvc.perform(post("/api/admin/quiz/event/modifier")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSkipQuestionEvent() throws Exception {
        mockMvc.perform(post("/api/admin/quiz/event/skip")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentQuestionIndex", notNullValue()));
    }
}