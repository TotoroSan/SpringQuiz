// File: `src/test/java/com/example/quiz/repository/UserRepositoryTest.java`
package com.example.quiz.repository;

import com.example.quiz.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindUser() {
        User user = new User();
        user.setUsername("unitTestUser");
        user.setEmail("unit@test.com");
        user.setPassword("password");
        User saved = userRepository.save(user);
        assertNotNull(saved.getId());

        User found = userRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("unitTestUser", found.getUsername());
    }

    @Test
    public void testFindByUsernameAndEmail() {
        User user = new User();
        user.setUsername("findTestUser");
        user.setEmail("find@test.com");
        user.setPassword("password");
        userRepository.save(user);

        User byUsername = userRepository.findByUsername("findTestUser").orElse(null);
        assertNotNull(byUsername);
        User byEmail = userRepository.findByEmail("find@test.com").orElse(null);
        assertNotNull(byEmail);
    }
}