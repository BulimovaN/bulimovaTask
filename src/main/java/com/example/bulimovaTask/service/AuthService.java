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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
 private final CustomMetricsService metrics;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenUtil jwtTokenUtil,
            CustomMetricsService metrics
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.metrics = metrics;
    }
    @Transactional(readOnly = true)
    public AuthResponseDTO login(AuthRequestDTO request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtTokenUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        return new AuthResponseDTO(token);
    }

    public UserResponseDTO createUser(UserCreaterDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new UserAlreadyExistsException(dto.getEmail());
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.ROLE_USER);

        User saved = userRepository.save(user);

        metrics.userCreated();

        return new UserResponseDTO(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getRole().name(),
                saved.getCreateDate(),
                saved.getUpdateDate()
        );
    }


}

