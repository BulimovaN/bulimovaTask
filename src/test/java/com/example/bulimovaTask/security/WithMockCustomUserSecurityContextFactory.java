package com.example.bulimovaTask.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Long userId = annotation.id();

        var authorities = List.of(
                new SimpleGrantedAuthority(annotation.role())
        );

        var authentication = new UsernamePasswordAuthenticationToken(
                userId,
                null,
                authorities
        );

        context.setAuthentication(authentication);
        return context;
    }
}

