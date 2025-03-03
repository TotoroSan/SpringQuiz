import com.example.quiz.model.dto.RegistrationRequestDto;
import com.example.quiz.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserRegistrationControllerTest {

    @InjectMocks
    private UserRegistrationController userRegistrationController;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        RegistrationRequestDto requestDto = new RegistrationRequestDto();
        requestDto.setUsername("testuser");
        requestDto.setEmail("testuser@example.com");
        requestDto.setPassword("securepassword");

        doNothing().when(userService).registerUser("testuser", "testuser@example.com", "securepassword");

        // Act
        ResponseEntity<String> response = userRegistrationController.registerUser(requestDto);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User registered successfully", response.getBody());
        verify(userService, times(1)).registerUser("testuser", "testuser@example.com", "securepassword");
    }

    @Test
    void testRegisterUser_EmailAlreadyInUse() {
        // Arrange
        RegistrationRequestDto requestDto = new RegistrationRequestDto();
        requestDto.setUsername("testuser");
        requestDto.setEmail("existinguser@example.com");
        requestDto.setPassword("securepassword");

        doThrow(new IllegalArgumentException("Email already in use"))
                .when(userService)
                .registerUser("testuser", "existinguser@example.com", "securepassword");

        // Act
        ResponseEntity<String> response = userRegistrationController.registerUser(requestDto);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Email already in use", response.getBody());
        verify(userService, times(1)).registerUser("testuser", "existinguser@example.com", "securepassword");
    }

    @Test
    void testRegisterUser_InvalidInput() {
        // Arrange
        RegistrationRequestDto requestDto = new RegistrationRequestDto();
        requestDto.setUsername("testuser");
        requestDto.setEmail(""); // Invalid email
        requestDto.setPassword("securepassword");

        doThrow(new IllegalArgumentException("Invalid email format"))
                .when(userService)
                .registerUser("testuser", "", "securepassword");

        // Act
        ResponseEntity<String> response = userRegistrationController.registerUser(requestDto);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid email format", response.getBody());
        verify(userService, times(1)).registerUser("testuser", "", "securepassword");
    }
}
