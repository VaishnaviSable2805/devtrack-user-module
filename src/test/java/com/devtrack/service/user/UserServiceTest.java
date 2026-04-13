package com.devtrack.service.user;

import com.devtrack.dto.user.LoginRequest;
import com.devtrack.dto.user.RegisterRequest;
import com.devtrack.entity.user.Role;
import com.devtrack.entity.user.User;
import com.devtrack.exception.InvalidCredentialsException;
import com.devtrack.exception.ResourceAlreadyExistsException;
import com.devtrack.repository.user.UserRepository;
import com.devtrack.service.impl.user.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerUser_shouldSaveUserSuccessfully() {
        RegisterRequest request = new RegisterRequest(
                "Vaish Sable",
                "vaish@example.com",
                "pass123",
                Role.DEVELOPER
        );

        when(userRepository.existsByEmail("vaish@example.com")).thenReturn(false);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setFullName(request.getFullName());
        savedUser.setEmail(request.getEmail());
        savedUser.setPassword(request.getPassword());
        savedUser.setRole(request.getRole());
        savedUser.setIsActive(true);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerUser(request);

        assertNotNull(result);
        assertEquals("Vaish Sable", result.getFullName());
        assertEquals("vaish@example.com", result.getEmail());
        assertEquals(Role.DEVELOPER, result.getRole());
        assertTrue(result.getIsActive());

        verify(userRepository, times(1)).existsByEmail("vaish@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_shouldThrowExceptionWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest(
                "Vaish Sable",
                "vaish@example.com",
                "pass123",
                Role.TESTER
        );

        when(userRepository.existsByEmail("vaish@example.com")).thenReturn(true);

        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> userService.registerUser(request)
        );

        assertEquals("User with this email already exists", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail("vaish@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginUser_shouldReturnUserWhenCredentialsAreCorrect() {
        LoginRequest loginRequest = new LoginRequest("vaish@example.com", "pass123");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setFullName("Vaish Sable");
        existingUser.setEmail("vaish@example.com");
        existingUser.setPassword("pass123");
        existingUser.setRole(Role.ADMIN);
        existingUser.setIsActive(true);

        when(userRepository.findByEmail("vaish@example.com")).thenReturn(Optional.of(existingUser));

        User result = userService.loginUser(loginRequest);

        assertNotNull(result);
        assertEquals("vaish@example.com", result.getEmail());
        assertEquals("pass123", result.getPassword());

        verify(userRepository, times(1)).findByEmail("vaish@example.com");
    }

    @Test
    void loginUser_shouldThrowExceptionWhenEmailIsNotFound() {
        LoginRequest loginRequest = new LoginRequest("unknown@example.com", "Password123");

        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> userService.loginUser(loginRequest)
        );

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("unknown@example.com");
    }

    @Test
    void loginUser_shouldThrowExceptionWhenPasswordIsWrong() {
        LoginRequest loginRequest = new LoginRequest("vaish@example.com", "WrongPassword");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setFullName("Vaish Sable");
        existingUser.setEmail("vaish@example.com");
        existingUser.setPassword("pass123");
        existingUser.setRole(Role.DEVELOPER);
        existingUser.setIsActive(true);

        when(userRepository.findByEmail("vaish@example.com")).thenReturn(Optional.of(existingUser));

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> userService.loginUser(loginRequest)
        );

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("vaish@example.com");
    }
}