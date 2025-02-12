package com.example.quiz.controller.user;

import com.example.quiz.model.dto.JwtResponseDto;
import com.example.quiz.model.dto.LoginRequestDto;
import com.example.quiz.model.entity.User;
import com.example.quiz.security.JwtTokenProvider;
import com.example.quiz.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    /**
     * Authenticates a user and generates a JWT token upon successful login.
     * If authentication fails, a 401 Unauthorized response is returned.
     *
     * @param loginRequestDto The login request containing username and password.
     * @return ResponseEntity containing the JWT token if authentication is successful, otherwise 401 Unauthorized.
     */
    @Operation(
            summary = "User Login",
            description = """
        Authenticates the user using the provided username and password.
        Returns a JWT token if authentication is successful.
        """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Authentication successful, JWT token returned",
            content = @Content(schema = @Schema(implementation = JwtResponseDto.class))
    )
    @ApiResponse(
            responseCode = "401",
            description = "Authentication failed (invalid credentials)",
            content = @Content
    )
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

    /**
     * Refreshes the JWT token for an authenticated user.
     * This endpoint generates a new JWT token using the authenticated user's details.
     *
     * @param user The currently authenticated user (extracted from the security context).
     * @return ResponseEntity containing the new JWT token if successful, or an error response otherwise.
     */
    @Operation(
            summary = "Refresh JWT Token",
            description = """
        Generates a new JWT token for an authenticated user. 
        The user must be logged in, and their authentication context must be valid.
        """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Token refreshed successfully",
            content = @Content(schema = @Schema(implementation = JwtResponseDto.class))
    )
    @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User is not authenticated or session expired",
            content = @Content
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error - Token refresh failed",
            content = @Content
    )
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



