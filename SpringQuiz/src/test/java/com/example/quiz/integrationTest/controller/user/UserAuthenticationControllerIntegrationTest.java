// File: src/integrationTest/java/com/example/quiz/controller/user/UserAuthenticationControllerIntegrationTest.java
package com.example.quiz.integrationTest.controller.user;

import com.example.quiz.model.dto.LoginRequestDto;
import com.example.quiz.model.dto.JwtResponseDto;
import com.example.quiz.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserAuthenticationControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    private final String loginUrl = "/user/api/auth/login";
    private final String refreshUrl = "/user/api/auth/refresh-token";

    @BeforeEach
    void setup() {
        // Register a user via the service so login can succeed.
        // If the user already exists, catching the exception is fine.
        try {
            userService.registerUser("testuser", "testuser@example.com", "password");
        } catch (IllegalArgumentException ignored) {}
    }

    @Test
    void testLoginSuccess() {
        LoginRequestDto loginDto = new LoginRequestDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("password");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequestDto> request = new HttpEntity<>(loginDto, headers);

        ResponseEntity<JwtResponseDto> response = restTemplate.postForEntity(loginUrl, request, JwtResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getToken());
    }

    @Test
    void testLoginFailure() {
        LoginRequestDto loginDto = new LoginRequestDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("wrongpassword");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequestDto> request = new HttpEntity<>(loginDto, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, request, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Authentication failed", response.getBody());
    }

    @Test
    void testRefreshTokenSuccess() {
        // First, login to obtain a JWT token.
        LoginRequestDto loginDto = new LoginRequestDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("password");
        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequestDto> loginRequest = new HttpEntity<>(loginDto, loginHeaders);

        ResponseEntity<JwtResponseDto> loginResponse = restTemplate.postForEntity(loginUrl, loginRequest, JwtResponseDto.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        String token = loginResponse.getBody().getToken();
        assertNotNull(token);

        // Now call the refresh endpoint with the Bearer token.
        HttpHeaders refreshHeaders = new HttpHeaders();
        refreshHeaders.set("Authorization", "Bearer " + token);
        HttpEntity<?> refreshRequest = new HttpEntity<>(refreshHeaders);

        ResponseEntity<JwtResponseDto> refreshResponse = restTemplate.exchange(refreshUrl, HttpMethod.POST, refreshRequest, JwtResponseDto.class);

        assertEquals(HttpStatus.OK, refreshResponse.getStatusCode());
        assertNotNull(refreshResponse.getBody());
        assertNotNull(refreshResponse.getBody().getToken());
    }

    @Test
    void testRefreshTokenUnauthorized() {
        // Call refresh-token without providing an Authorization header.
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(refreshUrl, HttpMethod.POST, request, String.class);

        // When no token is provided, the controller returns 401 Unauthorized.
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorized", response.getBody());
    }
}