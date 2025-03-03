package com.example.quiz.integrationTest.repository;

import com.example.quiz.model.entity.User;
import com.example.quiz.model.entity.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserProfileRepositoryIntegrationTest {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    public void setup() {
        // Create a test user for the UserProfile
        User user = new User();
        user.setUsername("testuser_profile");
        user.setEmail("profile@example.com");
        user.setPassword("password");
        testUser = userRepository.save(user);
    }

    @Test
    public void testSaveAndFindUserProfile() {
        // Create and save a new UserProfile associated with the testUser
        UserProfile profile = new UserProfile();
        profile.setId(testUser.getId());
        // Assume additional properties can be set, e.g. displayName and bio
        profile.setFirstName("Test DisplayName");
        profile.setBio("Test bio");

        UserProfile savedProfile = userProfileRepository.save(profile);
        assertNotNull(savedProfile.getId());

        // Retrieve using the custom finder
        UserProfile retrievedProfile = userProfileRepository.findByUserId(testUser.getId()).orElse(null);
        assertNotNull(retrievedProfile);
        assertEquals("Test DisplayName", retrievedProfile.getFirstName());
        assertEquals("Test bio", retrievedProfile.getBio());
    }

    @Test
    public void testUpdateUserProfile() {
        // Create and save a new UserProfile
        UserProfile profile = new UserProfile();
        profile.setId(testUser.getId());
        profile.setFirstName("Original Name");
        profile.setBio("Original bio");

        UserProfile savedProfile = userProfileRepository.save(profile);

        // Update fields
        savedProfile.setFirstName("Updated Name");
        savedProfile.setBio("Updated bio");
        userProfileRepository.save(savedProfile);

        // Retrieve and validate updated data
        UserProfile updatedProfile = userProfileRepository.findByUserId(testUser.getId()).orElse(null);
        assertNotNull(updatedProfile);
        assertEquals("Updated Name", updatedProfile.getFirstName());
        assertEquals("Updated bio", updatedProfile.getBio());
    }

    @Test
    public void testDeleteUserProfile() {
        // Create and save a new UserProfile
        UserProfile profile = new UserProfile();
        profile.setId(testUser.getId());
        profile.setFirstName("DeleteMe");
        profile.setBio("To be deleted");

        UserProfile savedProfile = userProfileRepository.save(profile);
        Long id = savedProfile.getId();
        assertNotNull(id);

        // Delete the profile
        userProfileRepository.delete(savedProfile);

        // Verify deletion
        UserProfile deletedProfile = userProfileRepository.findById(id).orElse(null);
        assertNull(deletedProfile);
    }
}