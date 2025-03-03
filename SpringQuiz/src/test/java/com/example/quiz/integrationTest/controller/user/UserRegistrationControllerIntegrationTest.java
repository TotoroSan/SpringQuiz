// File: src/integrationTest/java/com/example/quiz/controller/user/UserRegistrationControllerIntegrationTest.java
package com.example.quiz.integrationTest.controller.user;

import com.example.quiz.model.dto.RegistrationRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserRegistrationControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testRegisterUserSuccess() {
        RegistrationRequestDto dto = new RegistrationRequestDto();
        dto.setUsername("newuser");
        dto.setEmail("newuser@example.com");
        dto.setPassword("password");

        HttpEntity<RegistrationRequestDto> request = new HttpEntity<>(dto);
        ResponseEntity<String> response = restTemplate.postForEntity("/user/api/registration/register", request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("User registered successfully"));
    }

    @Test
    void testRegisterUserDuplicateEmail() {
        RegistrationRequestDto dto1 = new RegistrationRequestDto();
        dto1.setUsername("user1");
        dto1.setEmail("duplicate@example.com");
        dto1.setPassword("password1");

        RegistrationRequestDto dto2 = new RegistrationRequestDto();
        dto2.setUsername("user2");
        dto2.setEmail("duplicate@example.com");
        dto2.setPassword("password2");

        // First registration should succeed.
        HttpEntity<RegistrationRequestDto> request1 = new HttpEntity<>(dto1);
        ResponseEntity<String> response1 = restTemplate.postForEntity("/user/api/registration/register", request1, String.class);
        assertEquals(HttpStatus.OK, response1.getStatusCode());

        // Second registration should fail (duplicate email).
        HttpEntity<RegistrationRequestDto> request2 = new HttpEntity<>(dto2);
        ResponseEntity<String> response2 = restTemplate.postForEntity("/user/api/registration/register", request2, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }
}