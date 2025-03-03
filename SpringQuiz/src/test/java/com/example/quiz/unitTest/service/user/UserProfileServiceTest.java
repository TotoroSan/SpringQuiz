package com.example.quiz.unitTest.service.user;

import com.example.quiz.model.dto.UserProfileDto;
import com.example.quiz.model.entity.User;
import com.example.quiz.model.entity.UserProfile;
import com.example.quiz.repository.UserProfileRepository;
import com.example.quiz.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserProfileServiceTest {

    @InjectMocks
    private UserProfileService userProfileService;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByUserId_UserExistsWithProfile() {
        // Arrange
        Long userId = 1L;
        UserProfile userProfile = new UserProfile();
        User user = new User();
        user.setId(userId);
        user.setUserProfile(userProfile);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        Optional<UserProfile> result = userProfileService.findByUserId(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userProfile, result.get());
    }

    @Test
    void testFindByUserId_UserNotFound() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Optional<UserProfile> result = userProfileService.findByUserId(userId);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testUpdateUserProfile_UserExistsWithProfile() {
        // Arrange
        Long userId = 1L;
        UserProfile userProfile = new UserProfile();
        User user = new User();
        user.setId(userId);
        user.setUserProfile(userProfile);

        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setFirstName("John");
        userProfileDto.setLastName("Doe");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        Optional<UserProfile> result = userProfileService.updateUserProfile(userId, userProfileDto);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John", userProfile.getFirstName());
        assertEquals("Doe", userProfile.getLastName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUserProfile_UserNotFound() {
        // Arrange
        Long userId = 1L;
        UserProfileDto userProfileDto = new UserProfileDto();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Optional<UserProfile> result = userProfileService.updateUserProfile(userId, userProfileDto);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testCreateUserProfile_UserExists() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setFirstName("John");
        userProfileDto.setLastName("Doe");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        Optional<UserProfile> result = userProfileService.createUserProfile(userId, userProfileDto);

        // Assert
        assertTrue(result.isPresent());
        UserProfile createdProfile = result.get();
        assertEquals("John", createdProfile.getFirstName());
        assertEquals("Doe", createdProfile.getLastName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testCreateUserProfile_UserNotFound() {
        // Arrange
        Long userId = 1L;
        UserProfileDto userProfileDto = new UserProfileDto();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Optional<UserProfile> result = userProfileService.createUserProfile(userId, userProfileDto);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteUserProfile_ProfileExists() {
        // Arrange
        Long userId = 1L;
        UserProfile userProfile = new UserProfile();
        User user = new User();
        user.setId(userId);
        user.setUserProfile(userProfile);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        boolean result = userProfileService.deleteUserProfile(userId);

        // Assert
        assertTrue(result);
        verify(userProfileRepository, times(1)).delete(userProfile);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testDeleteUserProfile_ProfileDoesNotExist() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        boolean result = userProfileService.deleteUserProfile(userId);

        // Assert
        assertFalse(result);
        verify(userProfileRepository, never()).delete(any());
    }

    @Test
    void testDeleteUserProfile_UserNotFound() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        boolean result = userProfileService.deleteUserProfile(userId);

        // Assert
        assertFalse(result);
    }

    @Test
    void testConvertToDto() {
        // Arrange
        UserProfile userProfile = new UserProfile();
        userProfile.setId(1L);
        userProfile.setFirstName("John");
        userProfile.setLastName("Doe");
        userProfile.setDateOfBirth("24.09.1993");

        User user = new User();
        user.setId(2L);
        user.setEmail("john.doe@example.com");
        userProfile.setUser(user);

        // Act
        UserProfileDto result = userProfileService.convertToDto(userProfile);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), result.getDateOfBirth());
        assertEquals("john.doe@example.com", result.getEmail());
    }
}
