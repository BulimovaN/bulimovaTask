package com.example.bulimovaTask.service;

import com.example.bulimovaTask.component.JwtTokenUtil;
import com.example.bulimovaTask.configuration.CustomMetricsService;
import com.example.bulimovaTask.dto.AuthRequestDTO;
import com.example.bulimovaTask.dto.AuthResponseDTO;
import com.example.bulimovaTask.dto.UserCreaterDTO;
import com.example.bulimovaTask.dto.UserResponseDTO;
import com.example.bulimovaTask.entity.Role;
import com.example.bulimovaTask.entity.User;
import com.example.bulimovaTask.exception.UserAlreadyExistsException;
import com.example.bulimovaTask.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AuthServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtTokenUtil jwtTokenUtil;

    @Mock
    CustomMetricsService metrics;


    @InjectMocks
    AuthService authService;



    @Test
    @DisplayName("AUTH LOGIN: should return JWT token when credentials are valid")

    void loginShouldReturnTokenWhenCredentialsAreValid() {
        // Arrange
        AuthRequestDTO request = new AuthRequestDTO();
        request.setEmail("user@mail.com");
        request.setPassword("password");

        User user = new User();
        user.setId(1L);
        user.setEmail("user@mail.com");
        user.setPassword("encoded");
        user.setRole(Role.ROLE_USER);

        when(userRepository.findByEmail("user@mail.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded"))
                .thenReturn(true);
        when(jwtTokenUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        )).thenReturn("jwt-token");

        // Act
        AuthResponseDTO response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());

        verify(userRepository).findByEmail("user@mail.com");
        verify(passwordEncoder).matches("password", "encoded");
        verify(jwtTokenUtil).generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    @Test
    @DisplayName("AUTH LOGIN: should throw BadCredentialsException when user is not found")

    void loginShouldThrowExceptionWhenUserNotFound() {
        // Arrange
        AuthRequestDTO request = new AuthRequestDTO("wrong@mail.com", "password");

        when(userRepository.findByEmail("wrong@mail.com"))
                .thenReturn(Optional.empty());

        // Act + Assert
      assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verify(userRepository).findByEmail("wrong@mail.com");

    }

    @Test
    @DisplayName("AUTH LOGIN: should throw BadCredentialsException when password is invalid")

    void loginShouldThrowExceptionWhenPasswordIsInvalid() {
        // Arrange
        AuthRequestDTO request = new AuthRequestDTO("user@mail.com", "wrong");

        User user = new User();
        user.setPassword("encoded");

        when(userRepository.findByEmail("user@mail.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded"))
                .thenReturn(false);

        // Act + Assert
        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verify(passwordEncoder).matches("wrong", "encoded");
        verify(jwtTokenUtil, never()).generateToken(any(), any(), any());
    }

    @Test
    @DisplayName("AUTH REGISTER: should create user when email does not exist")

    void createUserShouldCreateUserWhenEmailNotExists() {
        // Arrange
        UserCreaterDTO dto = new UserCreaterDTO(
                "john",
                "john@mail.com",
                "password"
        );

        when(userRepository.existsByEmail("john@mail.com"))
                .thenReturn(false);
        when(passwordEncoder.encode("password"))
                .thenReturn("encoded");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("john");
        savedUser.setEmail("john@mail.com");
        savedUser.setRole(Role.ROLE_USER);

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        // Act
        UserResponseDTO response = authService.createUser(dto);

        // Assert
        assertNotNull(response);
        assertEquals("john", response.getUsername());
        assertEquals("john@mail.com", response.getEmail());
        assertEquals("ROLE_USER", response.getRole());


    }

    @Test
    @DisplayName("AUTH REGISTER: should throw UserAlreadyExistsException when email already exists")

    void createUserShouldThrowExceptionWhenEmailAlreadyExists() {
        // Arrange
        UserCreaterDTO dto = new UserCreaterDTO(
                "john",
                "john@mail.com",
                "password"
        );

        when(userRepository.existsByEmail("john@mail.com"))
                .thenReturn(true);

        // Act + Assert
        assertThrows(
                UserAlreadyExistsException.class,
                () -> authService.createUser(dto)
        );

        verify(userRepository).existsByEmail("john@mail.com");
        verifyNoInteractions(passwordEncoder);
    }

}
