package com.example.quiz.integrationTest.controller.user;

import com.example.quiz.model.dto.QuestionGameEventDto;
import com.example.quiz.model.dto.QuizStateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserQuizStateControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testuser")
    public void testStartQuiz() throws Exception {
        // Start a new quiz with GET mapping and correct path

        MvcResult result = mockMvc.perform(get("/user/api/quiz/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.currentRound").exists())
                .andReturn();

        // Parse response to QuizStateDto to verify object structure
        QuizStateDto quizStateDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                QuizStateDto.class);

        assertTrue(quizStateDto.isActive(), "New quiz should be active");
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testGetCurrentQuestion() throws Exception {
        // First start a quiz
        mockMvc.perform(post("/api/quiz/start"))
                .andExpect(status().isOk());

        // Then get current question
        mockMvc.perform(get("/api/quiz/questions/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionText").exists())
                .andExpect(jsonPath("$.shuffledAnswers").isArray())
                .andExpect(jsonPath("$.shuffledAnswers", hasSize(4)));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testApplyFiftyFiftyJoker() throws Exception {
        // Start a quiz
        mockMvc.perform(post("/api/quiz/start"))
                .andExpect(status().isOk());

        // Apply 50-50 joker through admin endpoint for testing
        mockMvc.perform(post("/api/admin/jokers/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"jokerType\":\"fifty-fifty\",\"tier\":1}"))
                .andExpect(status().isOk());

        // Verify joker effect is applied
        MvcResult currentQuestionResult = mockMvc.perform(get("/api/quiz/questions/current"))
                .andExpect(status().isOk())
                .andReturn();

        QuestionGameEventDto questionEvent = objectMapper.readValue(
                currentQuestionResult.getResponse().getContentAsString(),
                QuestionGameEventDto.class);

        // Should have 2 answers eliminated (50-50 joker)
        assertEquals(2, questionEvent.getAnswersToBeDeleted().size());
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testAnswerQuestion() throws Exception {
        // 1. Start quiz
        mockMvc.perform(post("/api/quiz/start"))
                .andExpect(status().isOk());

        // 2. Get current question
        MvcResult questionResult = mockMvc.perform(get("/api/quiz/questions/current"))
                .andExpect(status().isOk())
                .andReturn();

        QuestionGameEventDto questionEvent = objectMapper.readValue(
                questionResult.getResponse().getContentAsString(),
                QuestionGameEventDto.class);

        // 3. Answer with the first answer (could be correct or wrong, doesn't matter for test)
        Long answerId = questionEvent.getShuffledAnswers().get(0).getId();

        AnswerSubmissionDto answerSubmission = new AnswerSubmissionDto();
        answerSubmission.setAnswerId(answerId);

        mockMvc.perform(post("/api/quiz/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerSubmission)))
                .andExpect(status().isOk());

        // 4. Verify the answer was recorded
        mockMvc.perform(get("/api/quiz/state"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameEvents.length()").value(greaterThan(1)))
                .andExpect(jsonPath("$.gameEvents[1].type").value("ANSWER"));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testCompleteQuizFlow() throws Exception {
        // 1. Start quiz
        mockMvc.perform(post("/api/quiz/start"))
                .andExpect(status().isOk());

        // 2. Apply joker
        mockMvc.perform(post("/api/admin/jokers/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"jokerType\":\"fifty-fifty\",\"tier\":1}"))
                .andExpect(status().isOk());

        // 3. Get current question with joker applied
        MvcResult questionResult = mockMvc.perform(get("/api/quiz/questions/current"))
                .andExpect(status().isOk())
                .andReturn();

        QuestionGameEventDto questionEvent = objectMapper.readValue(
                questionResult.getResponse().getContentAsString(),
                QuestionGameEventDto.class);

        // Verify joker effect
        assertFalse(questionEvent.getAnswersToBeDeleted().isEmpty());

        // 4. Answer the question
        Long answerId = questionEvent.getShuffledAnswers().stream()
                .filter(a -> !questionEvent.getAnswersToBeDeleted().contains(a.getId()))
                .findFirst()
                .orElse(questionEvent.getShuffledAnswers().get(0))
                .getId();

        AnswerSubmissionDto answerSubmission = new AnswerSubmissionDto();
        answerSubmission.setAnswerId(answerId);

        mockMvc.perform(post("/api/quiz/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerSubmission)))
                .andExpect(status().isOk());

        // 5. Verify quiz progression
        mockMvc.perform(get("/api/quiz/state"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameEvents.length()").value(greaterThan(1)));
    }
}