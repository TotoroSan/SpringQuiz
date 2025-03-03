package com.example.quiz;

import com.example.quiz.model.dto.JwtResponseDto;
import com.example.quiz.model.dto.LoginRequestDto;
import com.example.quiz.model.dto.RegistrationRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private final String registrationUrl = "/user/api/registration/register";
    private final String loginUrl = "/user/api/auth/login";
    // A protected endpoint not explicitly defined in your controllers.
    // All endpoints not permitted in the security configuration are protected.
    private final String protectedUrl = "/user/api/protected/resource";

    @BeforeEach
    public void setupTestUser() {
        RegistrationRequestDto registration = new RegistrationRequestDto();
        registration.setUsername("testuser");
        registration.setEmail("testuser@example.com");
        registration.setPassword("password");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegistrationRequestDto> request = new HttpEntity<>(registration, headers);

        try {
            restTemplate.postForEntity(registrationUrl, request, String.class);
        } catch (Exception e) {
            // Ignore any exception (user may already be registered)
        }
    }

    @Test
    public void testProtectedEndpointWithoutAuth() {
        ResponseEntity<String> response = restTemplate.getForEntity(protectedUrl, String.class);
        // Expecting 401 Unauthorized since no token is provided.
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testProtectedEndpointWithAuth() {
        // Log in to obtain a valid JWT token.
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequestDto> loginEntity = new HttpEntity<>(loginRequest, loginHeaders);

        ResponseEntity<JwtResponseDto> loginResponse = restTemplate.postForEntity(loginUrl, loginEntity, JwtResponseDto.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody());
        String token = loginResponse.getBody().getToken();
        assertNotNull(token);

        // Access the protected endpoint with the Bearer token.
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", "Bearer " + token);
        HttpEntity<Void> authEntity = new HttpEntity<>(authHeaders);

        ResponseEntity<String> response = restTemplate.exchange(protectedUrl, HttpMethod.GET, authEntity, String.class);
        // Expecting 200 OK with valid authentication.
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}