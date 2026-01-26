package com.example.bulimovaTask.service;


import com.example.bulimovaTask.configuration.CustomMetricsService;
import com.example.bulimovaTask.dto.PageResponse;
import com.example.bulimovaTask.dto.UserResponseDTO;
import com.example.bulimovaTask.dto.UserUpdateDTO;
import com.example.bulimovaTask.entity.Role;
import com.example.bulimovaTask.entity.User;
import com.example.bulimovaTask.exception.InvalidRoleException;
import com.example.bulimovaTask.exception.UserHasTasksException;
import com.example.bulimovaTask.exception.UserNotFoundException;
import com.example.bulimovaTask.repository.TaskRepository;
import com.example.bulimovaTask.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


//@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserServiceTest {
    @MockBean
    UserRepository userRepository;

    @MockBean
    TaskRepository taskRepository;

    @MockBean
    CustomMetricsService metrics;

    @Autowired
    UserService userService;

    @Test
    @DisplayName("USER: should return user when user exists")
    void shouldReturnUserWhenFound() {
        // Arrange
        Long userId = 1L;

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("John Doe");
        mockUser.setEmail("john.doe@example.com");
        mockUser.setRole(Role.ROLE_USER);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(mockUser));

        // Act
        UserResponseDTO result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("John Doe", result.getUsername());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("ROLE_USER", result.getRole());

        verify(userRepository, times(1)).findById(userId);
    }


    @Test
    @DisplayName("USER: should throw UserNotFoundException when user does not exist")
    void shouldThrowExceptionWhenUserNotFound() {

        Long userId = 60L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());
        // Act
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserById(userId)
        );
        // Assert
        assertEquals(
                "User not found with id: 60",
                exception.getMessage()
        );

        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("USER SEARCH: should return users when no filters are applied")
    void findUsersShouldReturnUsersWhenNoFilters() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setEmail("u1@mail.com");
        user1.setRole(Role.ROLE_USER);

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("u2@mail.com");
        user2.setRole(Role.ROLE_ADMIN);

        Page<User> page = new PageImpl<>(List.of(user1, user2));

        when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        PageResponse<UserResponseDTO> response = userService.findUsers(
                null, null, null, null,
                null, null,
                null, null,
                Pageable.unpaged()
        );

        assertNotNull(response);
        assertEquals(2, response.getTotal());
        assertEquals(2, response.getContent().size());
        verify(userRepository, times(1))
                .findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("USER SEARCH: should return empty result when role filter is invalid")

    void findUsersShouldReturnEmptyWhenInvalidRole() {
        PageResponse<UserResponseDTO> response = userService.findUsers(
                null, null, null, "WRONG_ROLE",
                null, null,
                null, null,
                Pageable.unpaged()
        );

        assertNotNull(response);
        assertEquals(0, response.getTotal());
        assertTrue(response.getContent().isEmpty());

        verifyNoInteractions(userRepository);
    }
    @Test
    @DisplayName("USER SEARCH: should return empty result when role filter is invalid")

    void findUsersShouldReturnEmptyWhenInvalidDateFormat() {
        PageResponse<UserResponseDTO> response = userService.findUsers(
                null, null, null, null,
                "invalid-date", null,
                null, null,
                Pageable.unpaged()
        );

        assertEquals(0, response.getTotal());
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("USER SEARCH: should return empty result when createFrom is after createTo")
    void findUsersShouldReturnEmptyWhenCreateFromAfterCreateTo() {

        PageResponse<UserResponseDTO> response = userService.findUsers(
                null,
                null,
                null,
                null,
                "2024-02-01T10:00:00",
                "2024-01-01T10:00:00",
                null,
                null,
                Pageable.unpaged()
        );

        assertNotNull(response);
        assertEquals(0, response.getTotal());
        assertTrue(response.getContent().isEmpty());

        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("USER UPDATE: should update user when data is valid")
    void updateUserShouldUpdateUserWhenDataIsValid() {

        Long userId = 1L;

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("old");
        existingUser.setRole(Role.ROLE_USER);

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setUsername("new");
        dto.setRole("ROLE_ADMIN");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));
        when(userRepository.saveAndFlush(any(User.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        UserResponseDTO response = userService.updateUser(userId, dto);

        assertNotNull(response);
        assertEquals("new", response.getUsername());
        assertEquals("ROLE_ADMIN", response.getRole());

        verify(userRepository).saveAndFlush(existingUser);
        verify(metrics).userUpdated();
    }

    @Test
    @DisplayName("USER UPDATE: should throw InvalidRoleException when role is invalid")
    void updateUserShouldThrowExceptionWhenRoleIsInvalid() {

        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setRole("WRONG_ROLE");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        assertThrows(
                InvalidRoleException.class,
                () -> userService.updateUser(userId, dto)
        );

        verify(userRepository, never()).saveAndFlush(any());
        verifyNoInteractions(metrics);
    }

    @Test
    @DisplayName("USER UPDATE: should throw UserNotFoundException when user does not exist")
    void updateUser_shouldThrowException_whenUserNotFound() {
        // Arrange
        Long userId = 60L;

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setUsername("new-name");
        dto.setRole("ROLE_USER");

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // Act
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUser(userId, dto)
        );

        // Assert
        assertEquals(
                "User not found with id: 60",
                exception.getMessage()
        );

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(taskRepository);
    }


    @Test
    @DisplayName("USER DELETE: should delete user when user exists and has no tasks")
    void deleteUserShouldDeleteUserWhenNoTasks() {

        Long userId = 1L;

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(taskRepository.existsByUser_Id(userId))
                .thenReturn(false);

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
        verify(metrics).userDeleted();
    }

    @Test
    @DisplayName("USER DELETE: should throw UserHasTasksException when user has tasks")
    void deleteUserShouldThrowExceptionWhenUserHasTasks() {

        Long userId = 1L;

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(taskRepository.existsByUser_Id(userId))
                .thenReturn(true);

        assertThrows(
                UserHasTasksException.class,
                () -> userService.deleteUser(userId)
        );

        verify(userRepository, never()).deleteById(any());
        verifyNoInteractions(metrics);
    }

    @Test
    @DisplayName("USER DELETE: should throw UserNotFoundException when user does not exist")
    void deleteUserShouldThrowExceptionWhenUserNotFound() {

        Long userId = 99L;

        when(userRepository.existsById(userId))
                .thenReturn(false);

        assertThrows(
                UserNotFoundException.class,
                () -> userService.deleteUser(userId)
        );

        verifyNoInteractions(taskRepository, metrics);
    }


}

