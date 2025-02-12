package com.example.quiz.controller.user;

import com.example.quiz.model.dto.RegistrationRequestDto;
import com.example.quiz.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user/api/registration")
public class UserRegistrationController {

    @Autowired
    private UserService userService;

    /**
     * Registers a new user with the given registration details (username, email, and password).
     * If registration fails (e.g., invalid input, duplicate user), returns a 400 Bad Request
     * with an error message.
     *
     * @param registrationRequestDto A DTO containing the username, email, and password
     * @return A ResponseEntity with a success message (200) or an error message (400)
     */
    @Operation(
            summary = "Register a new user",
            description = """
        Creates a new user account in the system using the provided username, 
        email, and password. If registration is successful, returns a 200 OK status; 
        otherwise returns 400 with an error message.
        """
    )
    @ApiResponse(
            responseCode = "200",
            description = "User registered successfully"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Registration failed due to invalid input or duplicate user",
            content = @Content
    )
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegistrationRequestDto registrationRequestDto) {
        try {
            userService.registerUser(registrationRequestDto.getUsername(), registrationRequestDto.getEmail(), registrationRequestDto.getPassword());
            return ResponseEntity.ok("User registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}