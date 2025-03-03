package com.example.quiz.unitTest.service.user;
import com.example.quiz.model.entity.User;
import com.example.quiz.repository.UserRepository;
import com.example.quiz.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveUser() {
        // Arrange
        User user = new User();
        user.setPassword("plaintext-password");

        when(passwordEncoder.encode("plaintext-password")).thenReturn("encoded-password");
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User result = userService.saveUser(user);

        // Assert
        assertNotNull(result);
        assertEquals("encoded-password", result.getPassword());
        verify(passwordEncoder, times(1)).encode("plaintext-password");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testFindByUsername_UserExists() {
        // Arrange
        String username = "testuser";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.findByUsername(username);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testFindByUsername_UserDoesNotExist() {
        // Arrange
        String username = "nonexistent";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findByUsername(username);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testFindByEmail_UserExists() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.findByEmail(email);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testFindByEmail_UserDoesNotExist() {
        // Arrange
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findByEmail(email);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        String username = "newuser";
        String email = "newuser@example.com";
        String password = "plaintext-password";

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = userService.registerUser(username, email, password);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(email, result.getEmail());
        assertEquals("encoded-password", result.getPassword());
        assertEquals("USER", result.getRoles());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUserProfile());
        verify(userRepository, times(1)).existsByEmail(email);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_EmailAlreadyInUse() {
        // Arrange
        String email = "existinguser@example.com";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.registerUser("user", email, "password"));
        assertEquals("Email already in use", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }
}
