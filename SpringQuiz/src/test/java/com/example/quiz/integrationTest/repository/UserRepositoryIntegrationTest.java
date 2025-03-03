package com.example.quiz.integrationTest.repository;

import com.example.quiz.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindUser() {
        // Create a test user
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");

        // Save the user
        User savedUser = userRepository.save(user);
        assertNotNull(savedUser.getId());

        // Retrieve the user
        User retrievedUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertNotNull(retrievedUser);
        assertEquals("testuser", retrievedUser.getUsername());
        assertEquals("test@example.com", retrievedUser.getEmail());
    }

    @Test
    public void testFindByUsername() {
        // Create a test user with a unique username
        User user = new User();
        user.setUsername("findByUsernameTest");
        user.setEmail("findByUsername@example.com");
        user.setPassword("password");
        userRepository.save(user);

        // Test finding by username
        User foundUser = userRepository.findByUsername("findByUsernameTest").orElse(null);
        assertNotNull(foundUser);
        assertEquals("findByUsernameTest", foundUser.getUsername());
    }

    @Test
    public void testFindByEmail() {
        // Create a test user with a unique email
        User user = new User();
        user.setUsername("emailTest");
        user.setEmail("findByEmail@example.com");
        user.setPassword("password");
        userRepository.save(user);

        // Test finding by email
        User foundUser = userRepository.findByEmail("findByEmail@example.com").orElse(null);
        assertNotNull(foundUser);
        assertEquals("emailTest", foundUser.getUsername());
    }

    @Test
    public void testExistsByEmail() {
        // Create a test user
        User user = new User();
        user.setUsername("existsTest");
        user.setEmail("exists@example.com");
        user.setPassword("password");
        userRepository.save(user);

        // Test existence checks
        assertTrue(userRepository.existsByEmail("exists@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    public void testExistsByUsername() {
        // Create a test user
        User user = new User();
        user.setUsername("usernameExistsTest");
        user.setEmail("usernameExists@example.com");
        user.setPassword("password");
        userRepository.save(user);

        // Test existence checks
        assertTrue(userRepository.existsByUsername("usernameExistsTest"));
        assertFalse(userRepository.existsByUsername("nonexistentUser"));
    }
}