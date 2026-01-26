package com.example.bulimovaTask.exception;

import com.example.bulimovaTask.dto.ErrorResponseDTO;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidJson(
            HttpMessageNotReadableException ex
    ) {
        log.warn("Invalid JSON request body");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        "Invalid JSON request body"
                ));
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentials(
            BadCredentialsException ex
    ) {
        log.warn("Bad credentials");

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Invalid email or password"
                ));
    }


    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponseDTO> handleJwtException(
            JwtException ex
    ) {
        log.warn("JWT error: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Invalid or expired token"
                ));
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(
            AccessDeniedException ex
    ) {
        log.warn("Access denied");

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponseDTO(
                        HttpStatus.FORBIDDEN.value(),
                        "Access denied"
                ));
    }


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFound(
            UserNotFoundException ex
    ) {
        log.warn("User not found: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDTO(
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMessage()
                ));
    }


    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleTaskNotFound(
            TaskNotFoundException ex
    ) {
        log.warn("Task not found: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDTO(
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMessage()
                ));
    }


    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserAlreadyExists(
            UserAlreadyExistsException ex
    ) {
        log.warn("User already exists: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(
                        HttpStatus.CONFLICT.value(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(
            MethodArgumentNotValidException ex
    ) {
        log.warn("Validation failed");

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        errors
                ));
    }


    @ExceptionHandler(UserHasTasksException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserHasTasks(
            UserHasTasksException ex
    ) {
        log.warn("User has tasks and cannot be deleted");

        Map<String, String> errors = new HashMap<>();
        errors.put("user", "User has tasks and cannot be deleted");

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(
                        HttpStatus.CONFLICT.value(),
                        errors
                ));
    }


    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidRole(
            InvalidRoleException ex
    ) {
        log.warn("Invalid role: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        errors.put("role", "Allowed values: ROLE_USER, ROLE_ADMIN");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        errors
                ));
    }


    @ExceptionHandler(InvalidTaskStatusForDeleteException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidTaskStatus(
            InvalidTaskStatusForDeleteException ex
    ) {
        log.warn("Invalid task status for delete: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(
                        HttpStatus.CONFLICT.value(),
                        ex.getMessage()
                ));
    }


    @ExceptionHandler(InvalidTaskStatusTransitionException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidTransition(
            InvalidTaskStatusTransitionException ex
    ) {
        log.warn("Invalid task status transition: {}", ex.getMessage());

        return ResponseEntity.badRequest()
                .body(new ErrorResponseDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage()
                ));
    }


    @ExceptionHandler(InvalidTaskStatusException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidStatus(
            InvalidTaskStatusException ex
    ) {
        log.warn("Invalid task status: {}", ex.getMessage());

        return ResponseEntity.badRequest()
                .body(new ErrorResponseDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage()
                ));
    }


    @ExceptionHandler({
            DeadlineRequiredException.class,
            InvalidDeadlineDateException.class,
            DeadlineRequiresPendingStatusException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleDeadlineErrors(
            RuntimeException ex
    ) {
        log.warn("Deadline validation error: {}", ex.getMessage());

        return ResponseEntity.badRequest()
                .body(new ErrorResponseDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage()
                ));
    }


    @ExceptionHandler(UserTasksAccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserTasksAccessDenied(
            UserTasksAccessDeniedException ex
    ) {
        log.warn("User tasks access denied: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponseDTO(
                        HttpStatus.FORBIDDEN.value(),
                        ex.getMessage()
                ));
    }


    @ExceptionHandler(TaskUpdateAccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleTaskUpdateAccessDenied(
            TaskUpdateAccessDeniedException ex
    ) {
        log.warn("Task update access denied: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponseDTO(
                        HttpStatus.FORBIDDEN.value(),
                        ex.getMessage()
                ));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleAnyException(Exception ex) {

        log.error("Unhandled exception", ex); // 👈 stacktrace ТОЛЬКО ТУТ

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Internal server error"
                ));
    }
    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            DateTimeParseException.class,
            ConversionFailedException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleInvalidDateFormat(Exception ex) {

        log.warn("Invalid date format in request: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        "Invalid date format. Expected ISO-8601 (yyyy-MM-dd'T'HH:mm:ss)"
                ));
    }


}