package com.example.bulimovaTask.controller;

import com.example.bulimovaTask.component.JwtTokenUtil;
import com.example.bulimovaTask.dto.AuthRequestDTO;
import com.example.bulimovaTask.dto.AuthResponseDTO;
import com.example.bulimovaTask.dto.UserCreaterDTO;
import com.example.bulimovaTask.dto.UserResponseDTO;
import com.example.bulimovaTask.service.AuthService;
import com.example.bulimovaTask.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    private final JwtTokenUtil jwtTokenUtil;
    private final AuthService authService;

    public AuthController(UserService userService, JwtTokenUtil jwtTokenUtil, AuthService authService) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.authService = authService;
    }


    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(
            @Valid @RequestBody UserCreaterDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.createUser(dto));
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @RequestBody AuthRequestDTO request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

}

