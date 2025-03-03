// File: src/integrationTest/java/com/example/quiz/service/user/UserRegistrationServiceIntegrationTest.java
package com.example.quiz.integrationTest.service.user;

import com.example.quiz.model.entity.User;
import com.example.quiz.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserRegistrationServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testRegisterUserSuccess() {
        String username = "serviceuser";
        String email = "serviceuser@example.com";
        String password = "password";

        // Register user through service
        User registeredUser = userService.registerUser(username, email, password);

        // Verify user was persisted
        Optional<User> fetchedUser = userRepository.findByEmail(email);
        assertTrue(fetchedUser.isPresent());
        assertEquals(username, fetchedUser.get().getUsername());
        // Verify password was encoded
        assertTrue(passwordEncoder.matches(password, fetchedUser.get().getPassword()));
    }

    @Test
    void testRegisterUserDuplicateEmail() {
        String username1 = "dupuser1";
        String email = "dup@example.com";
        String password1 = "password1";

        String username2 = "dupuser2";
        String password2 = "password2";

        // Register first user
        userService.registerUser(username1, email, password1);

        // Attempt duplicate registration and expect an exception
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser(username2, email, password2)
        );

        assertEquals("Email already in use", exception.getMessage());
    }
}