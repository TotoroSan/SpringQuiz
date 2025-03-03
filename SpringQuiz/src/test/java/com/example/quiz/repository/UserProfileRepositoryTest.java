package com.example.quiz.repository;

import com.example.quiz.model.entity.User;
import com.example.quiz.model.entity.UserProfile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserProfileRepositoryTest {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindByUserId() {
        User user = new User();
        user.setUsername("profileTestUser");
        user.setEmail("profile@testing.com");
        user.setPassword("password");
        User savedUser = userRepository.save(user);

        UserProfile profile = new UserProfile();
        profile.setId(savedUser.getId());
        profile.setFirstName("Test Display");
        profile.setBio("Bio details");
        UserProfile savedProfile = userProfileRepository.save(profile);
        assertNotNull(savedProfile.getId());

        UserProfile found = userProfileRepository.findByUserId(savedUser.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("Test Display", found.getFirstName());
    }
}