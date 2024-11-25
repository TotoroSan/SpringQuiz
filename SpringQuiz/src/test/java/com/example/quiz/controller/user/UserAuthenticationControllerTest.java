package com.example.quiz.controller.user;

import com.example.quiz.model.dto.LoginRequestDto;
import com.example.quiz.model.dto.JwtResponseDto;
import com.example.quiz.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserAuthenticationControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserAuthenticationController userAuthenticationController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAuthenticateUser_Success() {
        // Arrange
        LoginRequestDto loginRequestDto = new LoginRequestDto("testUser", "test@test", "password");
        Authentication authentication = mock(Authentication.class);
        String jwtToken = "mockJwtToken";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn(jwtToken);

        // Act
        ResponseEntity<?> response = userAuthenticationController.authenticateUser(loginRequestDto);

        // Assert
        assertEquals(ResponseEntity.ok(new JwtResponseDto(jwtToken)), response);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, times(1)).generateToken(authentication);
    }

    @Test
    public void testAuthenticateUser_Failure() {
        // Arrange
        LoginRequestDto loginRequestDto = new LoginRequestDto("testUser", "email@memail", "wrongPassword");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new RuntimeException("Authentication failed"));

        // Act
        ResponseEntity<?> response = userAuthenticationController.authenticateUser(loginRequestDto);

        // Assert
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Authentication failed", response.getBody());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, never()).generateToken(any());
    }
}
