package com.example.bulimovaTask.controller;


import com.example.bulimovaTask.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;

import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.security.test.context.support.WithMockUser;



public class UserControllerTest extends BaseIntegrationTest {


    @Autowired
 MockMvc mockMvc;




    @Test
    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /users → returns all users sorted by id")
    void getUsersShouldReturnUsersWhenAdmin() throws Exception {

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(3))
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].email").value("admin@mail.com"))
                .andExpect(jsonPath("$.content[1].email").value("user1@mail.com"))
                .andExpect(jsonPath("$.content[2].email").value("user2@mail.com"));
    }

    @Test
    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @WithMockUser(roles = "ADMIN")
    void getUsersShouldFilterByEmail() throws Exception {

        mockMvc.perform(get("/api/v1/users")
                        .param("email", "user1@mail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.content[0].id").value(2));
    }

    @Test
    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @WithMockUser(roles = "ADMIN")
    void getUsers_shouldFilterByRole() throws Exception {

        mockMvc.perform(get("/api/v1/users")
                        .param("role", "ROLE_USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.content[*].role")
                        .value(org.hamcrest.Matchers.everyItem(
                                org.hamcrest.Matchers.equalTo("ROLE_USER")
                        )));
    }

    @Test
    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @WithMockUser(roles = "ADMIN")
    void getUsers_shouldReturnPaginated() throws Exception {

        mockMvc.perform(get("/api/v1/users")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.total").value(3));
    }

    @Test
    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @WithMockUser(roles = "ADMIN")
    void getUserById_shouldReturnUser() throws Exception {

        mockMvc.perform(get("/api/v1/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user1@mail.com"));
    }

    @Test
    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @WithMockUser(roles = "ADMIN")
    void getUserById_shouldReturn404() throws Exception {

        mockMvc.perform(get("/api/v1/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @WithMockUser(roles = "ADMIN")
    void updateUser_shouldUpdateUser() throws Exception {

        mockMvc.perform(put("/api/v1/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "updated",
                                  "role": "ROLE_ADMIN"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updated"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
    }

    @Test
    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @WithMockUser(roles = "ADMIN")
    void deleteUser_shouldDeleteUser() throws Exception {

        mockMvc.perform(delete("/api/v1/users/2"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/users/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @WithMockUser(roles = "USER")
    void getUsers_shouldReturn403_whenUserRole() throws Exception {

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void getUsers_shouldReturn401_whenNoAuth() throws Exception {

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isUnauthorized());
    }




}
