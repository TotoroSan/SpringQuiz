<<<<<<<< HEAD:SpringQuiz/src/test/java/com/example/quiz/controller/admin/AdminQuestionControllerTest.java
package com.example.quiz.controller.admin;
========
package com.example.quiz.unitTest.controller.admin;
>>>>>>>> 5234cadcc7e235fbc1e9c39b5f08340ea17707a7:SpringQuiz/src/test/java/com/example/quiz/unitTest/controller/admin/AdminQuestionControllerTest.java

import com.example.quiz.model.dto.QuestionDto;
import com.example.quiz.service.admin.AdminQuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminQuestionController.class)
public class AdminQuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminQuestionService adminQuestionService;

    // TODO IMPLEMENT
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetQuestionById() throws Exception {
        // Arrange
        QuestionDto question = new QuestionDto();
        question.setQuestionText("Sample Question");
        when(adminQuestionService.getQuestionById(1L)).thenReturn(question);

        // Act & Assert
        mockMvc.perform(get("/api/questions/1")
                        .with(csrf())  // Add CSRF toke
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionText").value("Sample Question"));

        verify(adminQuestionService, times(1)).getQuestionById(1L);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreateQuestion() throws Exception {
        // Arrange
        QuestionDto question = new QuestionDto();
        question.setQuestionText("New Question");
        when(adminQuestionService.createQuestionFromDto(any(QuestionDto.class))).thenReturn(question);

        // Act & Assert
        mockMvc.perform(post("/api/questions")
                        .with(csrf())  // Add CSRF toke
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"questionText\": \"New Question\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionText").value("New Question"));

        verify(adminQuestionService, times(1)).createQuestionFromDto(any(QuestionDto.class));
    }
}
