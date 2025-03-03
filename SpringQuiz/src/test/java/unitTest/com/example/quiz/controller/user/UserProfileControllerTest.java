package unitTest.com.example.quiz.controller.user;

import com.example.quiz.model.dto.UserProfileDto;
import com.example.quiz.model.entity.User;
import com.example.quiz.model.entity.UserProfile;
import com.example.quiz.service.user.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserProfileControllerTest {

    @InjectMocks
    private UserProfileController userProfileController;

    @Mock
    private UserProfileService userProfileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserProfile_Success() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        UserProfile userProfile = new UserProfile();
        UserProfileDto userProfileDto = new UserProfileDto();

        when(userProfileService.findByUserId(1L)).thenReturn(Optional.of(userProfile));
        when(userProfileService.convertToDto(userProfile)).thenReturn(userProfileDto);

        // Act
        ResponseEntity<UserProfileDto> response = userProfileController.getUserProfile(user);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(userProfileDto, response.getBody());
        verify(userProfileService, times(1)).findByUserId(1L);
    }

    @Test
    void testGetUserProfile_NotFound() {
        // Arrange
        User user = new User();
        user.setId(1L);

        when(userProfileService.findByUserId(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<UserProfileDto> response = userProfileController.getUserProfile(user);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        verify(userProfileService, times(1)).findByUserId(1L);
    }

    @Test
    void testUpdateUserProfile_Success() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        UserProfileDto userProfileUpdates = new UserProfileDto();
        UserProfile updatedUserProfile = new UserProfile();
        UserProfileDto updatedUserProfileDto = new UserProfileDto();

        when(userProfileService.updateUserProfile(1L, userProfileUpdates)).thenReturn(Optional.of(updatedUserProfile));
        when(userProfileService.convertToDto(updatedUserProfile)).thenReturn(updatedUserProfileDto);

        // Act
        ResponseEntity<UserProfileDto> response = userProfileController.updateUserProfile(user, userProfileUpdates);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedUserProfileDto, response.getBody());
        verify(userProfileService, times(1)).updateUserProfile(1L, userProfileUpdates);
    }

    @Test
    void testUpdateUserProfile_NotFound() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        UserProfileDto userProfileUpdates = new UserProfileDto();

        when(userProfileService.updateUserProfile(1L, userProfileUpdates)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<UserProfileDto> response = userProfileController.updateUserProfile(user, userProfileUpdates);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        verify(userProfileService, times(1)).updateUserProfile(1L, userProfileUpdates);
    }

    @Test
    void testCreateUserProfile_Success() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        UserProfileDto userProfileDto = new UserProfileDto();
        UserProfile createdUserProfile = new UserProfile();
        UserProfileDto createdUserProfileDto = new UserProfileDto();

        when(userProfileService.createUserProfile(1L, userProfileDto)).thenReturn(Optional.of(createdUserProfile));
        when(userProfileService.convertToDto(createdUserProfile)).thenReturn(createdUserProfileDto);

        // Act
        ResponseEntity<UserProfileDto> response = userProfileController.createUserProfile(user, userProfileDto);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(createdUserProfileDto, response.getBody());
        verify(userProfileService, times(1)).createUserProfile(1L, userProfileDto);
    }

    @Test
    void testCreateUserProfile_Failure() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        UserProfileDto userProfileDto = new UserProfileDto();

        when(userProfileService.createUserProfile(1L, userProfileDto)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<UserProfileDto> response = userProfileController.createUserProfile(user, userProfileDto);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        verify(userProfileService, times(1)).createUserProfile(1L, userProfileDto);
    }

    @Test
    void testDeleteUserProfile_Success() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userProfileService.deleteUserProfile(1L)).thenReturn(true);

        // Act
        ResponseEntity<String> response = userProfileController.deleteUserProfile(user);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User profile deleted successfully", response.getBody());
        verify(userProfileService, times(1)).deleteUserProfile(1L);
    }

    @Test
    void testDeleteUserProfile_NotFound() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userProfileService.deleteUserProfile(1L)).thenReturn(false);

        // Act
        ResponseEntity<String> response = userProfileController.deleteUserProfile(user);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        verify(userProfileService, times(1)).deleteUserProfile(1L);
    }
}
