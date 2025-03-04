package com.example.quiz.integrationTest.controller.admin;

import com.example.quiz.controller.admin.AdminQuestionController;
import com.example.quiz.model.dto.QuestionDto;
import com.example.quiz.service.admin.AdminQuestionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminQuestionController.class)
@ActiveProfiles("test")
public class AdminQuestionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminQuestionService adminQuestionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetQuestionById() throws Exception {
        // Arrange
        QuestionDto questionDto = new QuestionDto();
        questionDto.setQuestionText("What is 2+2?");
        questionDto.setRealAnswer("4");
        questionDto.setMockAnswers(Arrays.asList("3", "5", "6"));

        when(adminQuestionService.getQuestionById(1L)).thenReturn(questionDto);

        // Act & Assert
        mockMvc.perform(get("/admin/api/questions/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionText").value("What is 2+2?"))
                .andExpect(jsonPath("$.realAnswer").value("4"))
                .andExpect(jsonPath("$.mockAnswers.length()").value(3));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreateQuestion() throws Exception {
        // Arrange
        QuestionDto inputDto = new QuestionDto();
        inputDto.setQuestionText("What is 2+2?");
        inputDto.setRealAnswer("4");
        inputDto.setMockAnswers(Arrays.asList("3", "5", "6"));

        QuestionDto outputDto = new QuestionDto();
        outputDto.setQuestionText("What is 2+2?");
        outputDto.setRealAnswer("4");
        outputDto.setMockAnswers(Arrays.asList("3", "5", "6"));

        when(adminQuestionService.createQuestionFromDto(any(QuestionDto.class))).thenReturn(outputDto);

        // Act & Assert
        mockMvc.perform(post("/admin/api/questions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionText").value("What is 2+2?"))
                .andExpect(jsonPath("$.realAnswer").value("4"))
                .andExpect(jsonPath("$.mockAnswers.length()").value(3));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateQuestion() throws Exception {
        // Arrange
        QuestionDto inputDto = new QuestionDto();
        inputDto.setQuestionText("Updated question?");
        inputDto.setRealAnswer("Updated answer");
        inputDto.setMockAnswers(Arrays.asList("Wrong 1", "Wrong 2"));

        when(adminQuestionService.updateQuestionFromDto(eq(1L), any(QuestionDto.class))).thenReturn(inputDto);

        // Act & Assert
        mockMvc.perform(put("/admin/api/questions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionText").value("Updated question?"))
                .andExpect(jsonPath("$.realAnswer").value("Updated answer"))
                .andExpect(jsonPath("$.mockAnswers.length()").value(2));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testDeleteQuestion() throws Exception {
        // Arrange
        doNothing().when(adminQuestionService).deleteQuestionById(1L);

        // Act & Assert
        mockMvc.perform(delete("/admin/api/questions/1")
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}