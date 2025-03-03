package com.example.quiz.integrationTest.service.user;

import com.example.quiz.model.dto.UserProfileDto;
import com.example.quiz.model.entity.User;
import com.example.quiz.model.entity.UserProfile;
import com.example.quiz.repository.UserProfileRepository;
import com.example.quiz.repository.UserRepository;
import com.example.quiz.service.user.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserProfileServiceIntegrationTest {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    private User testUser;

    @BeforeEach
    void setup() {
        // Create and persist a test user without a profile.
        testUser = new User();
        testUser.setEmail("testuser@example.com");
        testUser.setUsername("testuser");
        testUser = userRepository.save(testUser);
    }

    @Test
    void testCreateUserProfile() {
        UserProfileDto dto = new UserProfileDto();
        dto.setFirstName("Alice");
        dto.setLastName("Smith");
        dto.setDateOfBirth(String.valueOf(LocalDate.of(1995, 5, 15)));
        dto.setAddress("123 Main St");
        dto.setPhoneNumber("555-1234");
        dto.setEmail("alice.smith@example.com");
        dto.setProfilePictureUrl("http://example.com/alice.jpg");
        dto.setBio("Test bio");
        dto.setSocialMediaLinks("http://twitter.com/alice");

        Optional<UserProfile> createdProfileOpt = userProfileService.createUserProfile(testUser.getId(), dto);
        assertTrue(createdProfileOpt.isPresent());
        UserProfile createdProfile = createdProfileOpt.get();

        // Verify that the profile is persisted via repository
        Optional<UserProfile> persistedProfile = userProfileRepository.findById(createdProfile.getId());
        assertTrue(persistedProfile.isPresent());
        assertEquals("Alice", persistedProfile.get().getFirstName());
        assertEquals("Smith", persistedProfile.get().getLastName());
    }

    @Test
    void testUpdateUserProfile() {
        // First, create a profile for the user.
        UserProfileDto createDto = new UserProfileDto();
        createDto.setFirstName("Bob");
        createDto.setLastName("Jones");
        createDto.setDateOfBirth(String.valueOf(LocalDate.of(1990, 1, 1)));
        createDto.setAddress("456 Elm St");
        createDto.setPhoneNumber("555-5678");
        createDto.setEmail("bob.jones@example.com");
        createDto.setProfilePictureUrl("http://example.com/bob.jpg");
        createDto.setBio("Initial bio");
        createDto.setSocialMediaLinks("http://twitter.com/bobjones");

        Optional<UserProfile> createdProfileOpt = userProfileService.createUserProfile(testUser.getId(), createDto);
        assertTrue(createdProfileOpt.isPresent());

        // Now perform an update.
        UserProfileDto updateDto = new UserProfileDto();
        updateDto.setFirstName("Bobby");
        updateDto.setLastName("Jones");
        updateDto.setDateOfBirth(String.valueOf(LocalDate.of(1990, 1, 1)));
        updateDto.setAddress("789 Oak Ave");
        updateDto.setPhoneNumber("555-9012");
        updateDto.setEmail("bob.jones@example.com");
        updateDto.setProfilePictureUrl("http://example.com/bobby.jpg");
        updateDto.setBio("Updated bio");
        updateDto.setSocialMediaLinks("http://twitter.com/bobbyj");
        Optional<UserProfile> updatedProfileOpt = userProfileService.updateUserProfile(testUser.getId(), updateDto);
        assertTrue(updatedProfileOpt.isPresent());
        UserProfile updatedProfile = updatedProfileOpt.get();

        // Verify changes via the repository.
        Optional<UserProfile> persistedProfile = userProfileRepository.findById(updatedProfile.getId());
        assertTrue(persistedProfile.isPresent());
        assertEquals("Bobby", persistedProfile.get().getFirstName());
        assertEquals("789 Oak Ave", persistedProfile.get().getAddress());
        assertEquals("Updated bio", persistedProfile.get().getBio());
    }

    @Test
    void testDeleteUserProfile() {
        // Create a profile first.
        UserProfileDto dto = new UserProfileDto();
        dto.setFirstName("Charlie");
        dto.setLastName("Brown");
        dto.setDateOfBirth(String.valueOf(LocalDate.of(1988, 12, 25)));
        dto.setAddress("321 Maple Rd");
        dto.setPhoneNumber("555-0000");
        dto.setEmail("charlie.brown@example.com");
        dto.setProfilePictureUrl("http://example.com/charlie.jpg");
        dto.setBio("Bio for Charlie");
        dto.setSocialMediaLinks("http://twitter.com/charliebrown");

        Optional<UserProfile> createdProfileOpt = userProfileService.createUserProfile(testUser.getId(), dto);
        assertTrue(createdProfileOpt.isPresent());
        Long profileId = createdProfileOpt.get().getId();

        // Delete the profile.
        boolean deleted = userProfileService.deleteUserProfile(testUser.getId());
        assertTrue(deleted);

        // Confirm deletion via repository.
        Optional<UserProfile> deletedProfile = userProfileRepository.findById(profileId);
        assertFalse(deletedProfile.isPresent());
    }
}