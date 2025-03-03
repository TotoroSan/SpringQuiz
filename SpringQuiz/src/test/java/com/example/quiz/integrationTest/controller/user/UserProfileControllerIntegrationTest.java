// file: src/test/java/com/example/quiz/controller/user/UserProfileControllerIntegrationTest.java
package com.example.quiz.integrationTest.controller.user;

import com.example.quiz.model.dto.UserProfileDto;
import com.example.quiz.model.entity.User;
import com.example.quiz.model.entity.UserProfile;
import com.example.quiz.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class UserProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@example.com");
        userRepository.save(testUser);
    }

    // Helper method for setting a mock authentication token
    private RequestPostProcessor getAuthToken(User user) {
        return authentication(new TestingAuthenticationToken(user, null));
    }

    @Test
    public void testGetUserProfile_Found() throws Exception {
        // Create a profile and assign it to testUser.
        UserProfile profile = new UserProfile();
        profile.setFirstName("John");
        profile.setLastName("Doe");
        testUser.setUserProfile(profile);
        userRepository.save(testUser);

        String response = mockMvc.perform(get("/user/api/profile")
                        .with(getAuthToken(testUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UserProfileDto profileDto = objectMapper.readValue(response, UserProfileDto.class);
        assertThat(profileDto.getFirstName()).isEqualTo("John");
        assertThat(profileDto.getLastName()).isEqualTo("Doe");
    }

    @Test
    public void testGetUserProfile_NotFound() throws Exception {
        // When no profile exists.
        mockMvc.perform(get("/user/api/profile")
                        .with(getAuthToken(testUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateUserProfile() throws Exception {
        // Create a profile for update.
        UserProfile profile = new UserProfile();
        profile.setFirstName("John");
        profile.setLastName("Doe");
        testUser.setUserProfile(profile);
        userRepository.save(testUser);

        UserProfileDto updateDto = new UserProfileDto();
        updateDto.setFirstName("Jane");
        updateDto.setLastName("Smith");
        String json = objectMapper.writeValueAsString(updateDto);

        String response = mockMvc.perform(put("/user/api/profile")
                        .with(getAuthToken(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UserProfileDto updatedDto = objectMapper.readValue(response, UserProfileDto.class);
        assertThat(updatedDto.getFirstName()).isEqualTo("Jane");
        assertThat(updatedDto.getLastName()).isEqualTo("Smith");
    }

    @Test
    public void testCreateUserProfile() throws Exception {
        // Ensure no profile exists.
        testUser.setUserProfile(null);
        userRepository.save(testUser);

        UserProfileDto dto = new UserProfileDto();
        dto.setFirstName("Alice");
        dto.setLastName("Wonderland");
        String json = objectMapper.writeValueAsString(dto);

        String response = mockMvc.perform(post("/user/api/profile")
                        .with(getAuthToken(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UserProfileDto createdDto = objectMapper.readValue(response, UserProfileDto.class);
        assertThat(createdDto.getFirstName()).isEqualTo("Alice");
        assertThat(createdDto.getLastName()).isEqualTo("Wonderland");
    }

    @Test
    public void testDeleteUserProfile() throws Exception {
        // Create a profile for deletion.
        UserProfile profile = new UserProfile();
        profile.setFirstName("Bob");
        profile.setLastName("Builder");
        testUser.setUserProfile(profile);
        userRepository.save(testUser);

        mockMvc.perform(delete("/user/api/profile")
                        .with(getAuthToken(testUser)))
                .andExpect(status().isOk());

        Optional<User> updatedUserOpt = userRepository.findById(testUser.getId());
        assertThat(updatedUserOpt).isPresent();
        assertThat(updatedUserOpt.get().getUserProfile()).isNull();
    }
}