package com.example.bulimovaTask.controller;


import com.example.bulimovaTask.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Sql(
        scripts = "/sql/cleanup.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public class AuthControllerTest  extends BaseIntegrationTest {


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
    @DisplayName("AUTH LOGIN: should return token when credentials are valid")
    void login_shouldReturnToken_whenCredentialsAreValid() throws Exception {

        String requestJson = """
        {
          "email": "admin@mail.com",
          "password": "12345678"
        }
        """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @DisplayName("AUTH LOGIN: should return 401 when password is invalid")
    void login_shouldReturn401_whenPasswordIsInvalid() throws Exception {

        String requestJson = """
            {
              "email": "admin@mail.com",
              "password": "wrong"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @DisplayName("AUTH LOGIN: should return 401 when user not found")
    void login_shouldReturn401_whenUserNotFound() throws Exception {

        String requestJson = """
            {
              "email": "no@mail.com",
              "password": "12345678"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    // ===================== REGISTER =====================

    @Test
    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @DisplayName("AUTH REGISTER: should create user when email does not exist")
    void register_shouldCreateUser_whenEmailNotExists() throws Exception {

        String requestJson = """
            {
              "username": "newuser",
              "email": "new@mail.com",
              "password": "12345678"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("new@mail.com"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @DisplayName("AUTH REGISTER: should return 400 when email already exists")
    void register_shouldReturn400_whenEmailExists() throws Exception {

        String requestJson = """
            {
              "username": "admin",
              "email": "admin@mail.com",
              "password": "12345678"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already exists: admin@mail.com"));
    }


    @Test
    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @DisplayName("AUTH REGISTER: should return 400 when username too short")
    void register_shouldReturn400_whenUsernameTooShort() throws Exception {

        String json = """
        {
          "username": "a",
          "email": "new@mail.com",
          "password": "password123"
        }
        """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message.username")
                        .value("Username must be between 2 and 30 characters"));
    }

    @Test
    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @DisplayName("AUTH REGISTER: should return 400 when username too long")
    void register_shouldReturn400_whenUsernameTooLong() throws Exception {

        String json = """
        {
          "username": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
          "email": "new@mail.com",
          "password": "password123"
        }
        """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message.username")
                        .value("Username must be between 2 and 30 characters"));
    }

    @Test
    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @DisplayName("AUTH REGISTER: should return 400 when email is blank")
    void register_shouldReturn400_whenEmailBlank() throws Exception {

        String json = """
        {
          "username": "newuser",
          "email": "",
          "password": "password123"
        }
        """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message.email")
                        .value("Email must not be blank"));
    }

    @Test
    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @DisplayName("AUTH REGISTER: should return 400 when email invalid")
    void register_shouldReturn400_whenEmailInvalid() throws Exception {

        String json = """
        {
          "username": "newuser",
          "email": "not-an-email",
          "password": "password123"
        }
        """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message.email")
                        .value("Email must be valid"));
    }




    @Test
    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @DisplayName("AUTH REGISTER: should return 400 when password too short")
    void register_shouldReturn400_whenPasswordTooShort() throws Exception {

        String json = """
        {
          "username": "newuser",
          "email": "new@mail.com",
          "password": "pass"
        }
        """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message.password")
                        .value("Password must be at least 8 characters long"));
    }

}
