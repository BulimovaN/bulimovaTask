package com.example.bulimovaTask.configuration;

import com.example.bulimovaTask.entity.Role;
import com.example.bulimovaTask.entity.User;
import com.example.bulimovaTask.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("!test")
public class AdminInitializer {

    @Bean
    CommandLineRunner initAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${admin.email}") String email,
            @Value("${admin.password}") String password,
            @Value("${admin.username}") String username
    ) {
        return args -> {

            if (!userRepository.existsByEmail(email)) {

                User admin = new User();
                admin.setUsername(username);
                admin.setEmail(email);
                admin.setPassword(passwordEncoder.encode(password));
                admin.setRole(Role.ROLE_ADMIN);

                userRepository.save(admin);
            }
        };
    }
}