package org.guidewire.login.exceptions;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(@NotNull Exception ex) {
        logger.error("internal server error: {}", ex.getMessage());
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("message", "Something went wrong. Please try again.");
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(@NotNull RuntimeException ex) {
        logger.error("bad request: {}", ex.getMessage());
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("message", "Something went wrong. Please try again.");
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(@NotNull UserNotFoundException ex) {
        return getMapResponseEntity(ex, HttpStatus.NOT_FOUND, "User not found");
    }

    @ExceptionHandler(UserNameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, Object>> handleUserExists(@NotNull UserNameAlreadyExistsException ex) {
        return getMapResponseEntity(ex, HttpStatus.CONFLICT, "Username already exists");
    }

    @ExceptionHandler(PermissionNameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, Object>> handlePermissionExists(@NotNull PermissionNameAlreadyExistsException ex) {
        return getMapResponseEntity(ex, HttpStatus.CONFLICT, "Permission already exists");
    }

    @ExceptionHandler(PermissionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handlePermissionNotFound(@NotNull PermissionNotFoundException ex) {
        return getMapResponseEntity(ex, HttpStatus.NOT_FOUND, "Permission not found");
    }

    @ExceptionHandler(RoleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handlePermissionNotFound(@NotNull RoleNotFoundException ex) {
        return getMapResponseEntity(ex, HttpStatus.NOT_FOUND, "Role not found");
    }

    @NotNull
    private ResponseEntity<Map<String, Object>> getMapResponseEntity(@NotNull Exception ex,
                                                                     HttpStatusCode statusCode,
                                                                     String exceptionMessage) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("status", statusCode.value());
        logger.error("{}: {}", exceptionMessage, ex.getMessage());
        return new ResponseEntity<>(errorResponse, statusCode);
    }
}
