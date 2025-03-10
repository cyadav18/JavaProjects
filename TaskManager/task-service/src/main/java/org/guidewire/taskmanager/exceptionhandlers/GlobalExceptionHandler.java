package org.guidewire.taskmanager.exceptionhandlers;


import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle generic exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        logger.error("Internal server error: {}", ex.getMessage(), ex);
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
    }

    /**
     * Handle runtime exceptions.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        logger.error("Bad request: {}", ex.getMessage(), ex);
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, "Invalid request.");
    }

    /**
     * Handle validation errors for @Valid request bodies.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        logger.error("Validation error: {}", ex.getMessage());

        Map<String, Object> errors = new HashMap<>();
        errors.put("timestamp", LocalDateTime.now());
        errors.put("status", HttpStatus.BAD_REQUEST.value());
        errors.put("message", "Validation failed");

        Map<String, String> validationErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        errors.put("errors", validationErrors);

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }


    /**
     * Handle NotFoundException.
     */
    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleTaskNotFoundException(TaskNotFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, "Task not found.");
    }

    @ExceptionHandler(SubTaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleSubTaskNotFound(@NotNull SubTaskNotFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND,"Sub task not found.");
    }

    @ExceptionHandler(CommentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleCommentNotFound(@NotNull CommentNotFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND,"Comment not found.");
    }

    @ExceptionHandler(AttachmentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleAttachmentNotFound(@NotNull AttachmentNotFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND,"Attachment not found.");
    }

    @ExceptionHandler(NoTasksFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleAttachmentNotFound(@NotNull NoTasksFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND,"Tasks not found.");
    }


    /**
     * Utility method to create structured error responses.
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(Exception ex, HttpStatusCode statusCode, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("message", message);
        errorResponse.put("status", statusCode.value());
        logger.error("{}: {}", message, ex.getMessage());
        return new ResponseEntity<>(errorResponse, statusCode);
    }
}
