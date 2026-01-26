package com.example.bulimovaTask.controller;

import com.example.bulimovaTask.dto.UserResponseDTO;
import com.example.bulimovaTask.dto.UserUpdateDTO;
import com.example.bulimovaTask.dto.PageResponse;
import com.example.bulimovaTask.service.TaskService;
import com.example.bulimovaTask.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final TaskService taskService;

    public UserController(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<PageResponse<UserResponseDTO>> getUsers(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role,

            @RequestParam(required = false) String createFrom,
            @RequestParam(required = false) String createTo,
            @RequestParam(required = false) String updateFrom,
            @RequestParam(required = false) String updateTo,

            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        Pageable pageable = (page == null || size == null)
                ? Pageable.unpaged()
                : PageRequest.of(
                        page,
                size,
                Sort.by(Sort.Direction.ASC,"id"));

        return ResponseEntity.ok(
                userService.findUsers(
                        id, username, email, role,
                        createFrom, createTo,
                        updateFrom, updateTo,
                        pageable
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid
            @RequestBody UserUpdateDTO dto
    ) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



}





