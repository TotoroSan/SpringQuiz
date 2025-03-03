package com.example.quiz.unitTest.controller.user;

import com.example.quiz.model.dto.JwtResponseDto;
import com.example.quiz.model.dto.LoginRequestDto;
import com.example.quiz.model.entity.User;
import com.example.quiz.integrationTest.SecurityIntegrationTest.JwtTokenProvider;
import com.example.quiz.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserAuthenticationControllerTest {

    @InjectMocks
    private UserAuthenticationController userAuthenticationController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAuthenticateUser_Success() {
        // Arrange
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("testuser");
        loginRequestDto.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("test-jwt-token");

        // Act
        ResponseEntity<?> response = userAuthenticationController.authenticateUser(loginRequestDto);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof JwtResponseDto);
        assertEquals("test-jwt-token", ((JwtResponseDto) response.getBody()).getToken());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, times(1)).generateToken(authentication);
    }

    @Test
    void testAuthenticateUser_Failure() {
        // Arrange
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("testuser");
        loginRequestDto.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // Act
        ResponseEntity<?> response = userAuthenticationController.authenticateUser(loginRequestDto);

        // Assert
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Authentication failed", response.getBody());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, never()).generateToken(any(Authentication.class));
    }

    @Test
    void testRefreshToken_Success() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("new-jwt-token");

        // Act
        ResponseEntity<?> response = userAuthenticationController.refreshToken(user);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof JwtResponseDto);
        assertEquals("new-jwt-token", ((JwtResponseDto) response.getBody()).getToken());
        verify(jwtTokenProvider, times(1)).generateToken(any(Authentication.class));
    }

    @Test
    void testRefreshToken_UserNull() {
        // Act
        ResponseEntity<?> response = userAuthenticationController.refreshToken(null);

        // Assert
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Unauthorized", response.getBody());
        verify(jwtTokenProvider, never()).generateToken(any(Authentication.class));
    }

    @Test
    void testRefreshToken_Error() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");

        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenThrow(new RuntimeException("Unexpected error"));

        // Act
        ResponseEntity<?> response = userAuthenticationController.refreshToken(user);

        // Assert
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("An error occurred while refreshing the token", response.getBody());
        verify(jwtTokenProvider, times(1)).generateToken(any(Authentication.class));
    }
}
