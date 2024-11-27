package com.example.quiz.controller.user;

import com.example.quiz.model.dto.LoginRequestDto;
import com.example.quiz.model.dto.JwtResponseDto;
import com.example.quiz.model.entity.User;
import com.example.quiz.security.JwtTokenProvider;
import com.example.quiz.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user/api/auth")
public class UserAuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(UserAuthenticationController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    // generates a jwt token on successfull login and rerturns it
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDto loginRequestDto) {

        try {
            logger.info("Attempting to authenticate user: {}", loginRequestDto.getUsername());

            // this depends on the UserDetails that are specified in the security config
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getUsername(), // if we want to use email instead of username for authentication change here
                            loginRequestDto.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenProvider.generateToken(authentication);

            logger.info("Authentication successful for user: {}", loginRequestDto.getUsername());
            return ResponseEntity.ok(new JwtResponseDto(jwt));
        } catch (Exception ex) {
            logger.error("Authentication failed: {}", ex.getMessage());
            return ResponseEntity.status(401).body("Authentication failed");
        }
    }

    // Refresh token endpoint
    // Refresh token endpoint using @AuthenticationPrincipal
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@AuthenticationPrincipal User user) {
        try {
            logger.info("Attempting to refresh token for user: {}", user.getUsername());

            if (user == null) {
                logger.warn("Token refresh attempt failed because user is null");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }

            // Generate a new token using the authenticated user's details
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities()
            );

            String newToken = jwtTokenProvider.generateToken(authentication);
            logger.info("Successfully refreshed token for user: {}", user.getUsername());
            return ResponseEntity.ok(new JwtResponseDto(newToken));

        } catch (Exception ex) {
            logger.warn("An error occurred while refreshing the token");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while refreshing the token");
        }
    }
}



