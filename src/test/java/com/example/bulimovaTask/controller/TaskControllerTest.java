package com.example.bulimovaTask.controller;

//import com.example.bulimovaTask.old.AbstractIntegrationTest;
import com.example.bulimovaTask.BaseIntegrationTest;
import com.example.bulimovaTask.security.WithMockCustomUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;




class TaskControllerTest extends BaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;



    @Test

    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql",
                    "/sql/tasks.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @WithMockCustomUser(role = "ROLE_ADMIN")
    @DisplayName("TASK GET ALL: should return all tasks")
    void getAllTasks_shouldReturnTasks() throws Exception {

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(3))
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].title").value("Task 1"));
    }

    @Test

    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql",
                    "/sql/tasks.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @WithMockCustomUser(id = 1L, role = "ROLE_ADMIN")
    @DisplayName("TASK GET BY ID: owner can access task")
    void getTaskById_shouldReturnTask_whenOwner() throws Exception {

        mockMvc.perform(get("/api/v1/tasks/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Task 1"))
                .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test

    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql",
                    "/sql/tasks.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @WithMockCustomUser(id = 2L, role = "ROLE_USER")
    @DisplayName("TASK GET: should return 403")
    void getTask_shouldReturn403_whenNotOwner() throws Exception {

        mockMvc.perform(get("/api/v1/tasks/{id}", 1))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message")
                        .value("You can view only your own tasks"));
    }

    @Test

    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql",
                    "/sql/tasks.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @WithMockCustomUser(id = 2L, role = "ROLE_USER")
    @DisplayName("TASK GET: should return 404 when task not found")
    void getTask_shouldReturn404_whenNotFound() throws Exception {

        mockMvc.perform(get("/api/v1/tasks/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Task not found with id = 999"));
    }

    @Test

    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql",
                    "/sql/tasks.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @WithMockCustomUser(id = 2L, role = "ROLE_USER")
    @DisplayName("TASK CREATE: should create task")
    void createTask_shouldCreateTask() throws Exception {

        String body = """
            {
              "title": "New Task",
              "description": "My task"
            }
            """;

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test

    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql",
                    "/sql/tasks.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @WithMockCustomUser(id = 2L, role = "ROLE_USER")
    @DisplayName("TASK CREATE: should return 400 when title blank")
    void createTask_shouldReturn400_whenTitleBlank() throws Exception {

        String body = """
            {
              "title": "a",
              "description": "desc"
            }
            """;

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message.title")
                        .value("Title must be between 2 and 30 characters"));
    }


    @Test

    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql",
                    "/sql/tasks.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @WithMockCustomUser(id = 1L, role = "ROLE_ADMIN")
    @DisplayName("TASK DELETE: admin can delete NEW task")
    void deleteTask_shouldDeleteTask_whenAdmin() throws Exception {

        mockMvc.perform(delete("/api/v1/tasks/{id}", 1))
                .andExpect(status().isNoContent());
    }

    @Test

    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql",
                    "/sql/tasks.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @WithMockCustomUser(id = 2L, role = "ROLE_USER")
    @DisplayName("TASK DELETE: User cannot delete NEW task.")
    void deleteTask_shouldDeleteTask403_whenAdmin() throws Exception {

        mockMvc.perform(delete("/api/v1/tasks/{id}", 1))
        .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message")
                        .value("Access denied"));
    }

    @Test

    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql",
                    "/sql/tasks.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @WithMockCustomUser(id = 1L, role = "ROLE_ADMIN")
    @DisplayName("TASK DELETE: should return 404 when task not found")
    void deleteTask_shouldReturn404_whenNotFound() throws Exception {

        mockMvc.perform(delete("/api/v1/tasks/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Task not found with id = 999"));
    }

    @Test

    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql",
                    "/sql/tasks.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @WithMockCustomUser(id = 1L, role = "ROLE_ADMIN")
    @DisplayName("TASK DELETE: cannot delete task not in NEW status")
    void deleteTask_shouldReturn400_whenStatusNotNew() throws Exception {

        mockMvc.perform(delete("/api/v1/tasks/{id}", 2)) // DONE
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Invalid task status for delete: DONE"));
    }

    @Test

    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql",
                    "/sql/tasks.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @WithMockCustomUser(id = 2L, role = "ROLE_USER") // владелец Task 3
    @DisplayName("TASK UPDATE: should update task when data is valid (NEW → PENDING)")
    void updateTask_shouldUpdateTask_whenValid() throws Exception {

        String body = """
        {
          "title": "Updated title",
          "description": "Updated description",
          "status": "PENDING",
          "deadline": "2099-01-01T10:00:00"
        }
        """;

        mockMvc.perform(put("/api/v1/tasks/{id}", 3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.title").value("Updated title"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.deadline").exists())
                .andExpect(jsonPath("$.user.id").value(2))
                .andExpect(jsonPath("$.user.username").value("user1"));
    }

    @Test

    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql",
                    "/sql/tasks.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @WithMockCustomUser(id = 2L, role = "ROLE_USER")
    @DisplayName("TASK UPDATE: user cannot update someone else's task")
    void updateTask_shouldReturn403_whenNotOwner() throws Exception {

        String body = """
        {
          "title": "Updated title",
          "description": "Updated description",
          "status": "PENDING",
          "deadline": "2099-01-01T10:00:00"
        }
        """;

        mockMvc.perform(put("/api/v1/tasks/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message")
                        .value("You can edit only your own tasks"));
    }

    @Test

    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql",
                    "/sql/tasks.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @WithMockCustomUser(id = 1L, role = "ROLE_ADMIN")
    @DisplayName("TASK UPDATE: should return 404 when task not found")
    void updateTask_shouldReturn404_whenNotFound() throws Exception {

        String body = """
        {
          "title": "Title",
          "description": "Desc",
          "status": "NEW"
        }
        """;

        mockMvc.perform(put("/api/v1/tasks/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Task not found with id = 999"));
    }

    @Test

    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql",
                    "/sql/tasks.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @WithMockCustomUser(id = 1L, role = "ROLE_ADMIN")
    @DisplayName("TASK UPDATE: cannot change NEW → DONE")
    void updateTask_shouldReturn400_whenNewToDone() throws Exception {

        String body = """
        {
          "title": "Task 1",
          "description": "Desc",
          "status": "DONE"
        }
        """;

        mockMvc.perform(put("/api/v1/tasks/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid task status transition: NEW -> DONE"));
    }

    @Test

    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql",
                    "/sql/tasks.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @WithMockCustomUser(id = 1L, role = "ROLE_ADMIN")
    @DisplayName("TASK UPDATE: NEW → PENDING without deadline should fail")
    void updateTask_shouldReturn400_whenPendingWithoutDeadline() throws Exception {

        String body = """
        {
          "title": "Task 1",
          "description": "Desc",
          "status": "PENDING"
        }
        """;

        mockMvc.perform(put("/api/v1/tasks/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Deadline is required"));
    }

    @Test

    @Sql(
            scripts = {
                    "/sql/cleanup.sql",
                    "/sql/users.sql",
                    "/sql/tasks.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @WithMockCustomUser(id = 1L, role = "ROLE_ADMIN")
    @DisplayName("TASK UPDATE: NEW → NEW with deadline should fail")
    void updateTask_shouldReturn400_whenNewWithDeadline() throws Exception {

        String body = """
        {
          "title": "Task 1",
          "description": "Desc",
          "status": "NEW",
          "deadline": "2099-01-01T10:00:00"
        }
        """;

        mockMvc.perform(put("/api/v1/tasks/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Deadline can be set only when task status is PENDING"));
    }

}




