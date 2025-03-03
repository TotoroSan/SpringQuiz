package com.example.quiz.integrationTest.controller.admin;

import com.example.quiz.model.dto.QuizStateDto;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.repository.QuizStateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AdminQuizStateControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QuizStateRepository quizStateRepository;

    private QuizState dummyQuizState;

    @BeforeEach
    void setup() {
        // create and save a dummy QuizState for user id = 1
        dummyQuizState = new QuizState(1L);
        dummyQuizState.setActive(true);
        dummyQuizState.setCurrentQuestionIndex(0);
        dummyQuizState.setScore(0);
        quizStateRepository.save(dummyQuizState);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testStartQuiz() throws Exception {
        mockMvc.perform(post("/admin/api/quiz/start")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testForceCompleteQuiz() throws Exception {
        // Using the saved quiz state id in the URL as a dummy value.
        mockMvc.perform(put("/admin/api/quiz/" + dummyQuizState.getId() + "/complete")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateCurrentQuestion() throws Exception {
        int newIndex = 2;
        mockMvc.perform(put("/admin/api/quiz/" + dummyQuizState.getId() + "/question/" + newIndex)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentQuestionIndex").value(newIndex));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateScore() throws Exception {
        int newScore = 50;
        mockMvc.perform(put("/admin/api/quiz/" + dummyQuizState.getId() + "/score/" + newScore)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(newScore));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetQuizState() throws Exception {
        mockMvc.perform(get("/admin/api/quiz/state")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testResetQuizState() throws Exception {
        mockMvc.perform(post("/admin/api/quiz/reset")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testForceQuizEnd() throws Exception {
        mockMvc.perform(post("/admin/api/quiz/end")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testTestJokerEffect() throws Exception {
        // Assuming joker test endpoint returns quizState DTO on success.
        mockMvc.perform(post("/admin/api/quiz/joker/test")
                        .param("jokerIdString", "FIFTY_FIFTY")
                        .param("tier", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()));
    }
}