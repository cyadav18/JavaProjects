package org.guidewire.taskmanager.controller;

import jakarta.validation.Valid;
import org.guidewire.taskmanager.dto.request.SubTaskRequest;
import org.guidewire.taskmanager.dto.response.SubTaskResponse;
import org.guidewire.taskmanager.exceptionhandlers.SubTaskNotFoundException;
import org.guidewire.taskmanager.exceptionhandlers.TaskNotFoundException;
import org.guidewire.taskmanager.services.SubTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/task-manager/subtasks")

public class SubTaskController {

    private static final Logger logger = LoggerFactory.getLogger(SubTaskController.class);
    private final SubTaskService subTaskService;

    public SubTaskController(SubTaskService subTaskService) {
        this.subTaskService = subTaskService;
    }

    /**
     * Create a subtask for a given task.
     */
    @PostMapping("/{taskId}/create")
    public ResponseEntity<?> createSubTask(@PathVariable UUID taskId, @RequestBody SubTaskRequest subTaskRequest) {
        try {
            logger.info("createSubTask: Creating subtask for task '{}'", taskId);
            SubTaskResponse response = subTaskService.createSubTask(taskId, subTaskRequest);
            logger.info("createSubTask: Successfully created subtask '{}' for task '{}'", response.getId(), taskId);
            return ResponseEntity.ok(response);
        } catch (TaskNotFoundException e) {
            logger.warn("createSubTask: Task not found '{}'", taskId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("createSubTask: Unexpected error while creating subtask for task '{}'", taskId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unexpected error occurred."));
        }
    }

    /**
     * Retrieve subtasks for a given task.
     */
    @GetMapping("/{taskId}/list")
    public ResponseEntity<?> getSubTasks(@PathVariable UUID taskId) {
        try {
            logger.info("getSubTasks: Fetching subtasks for task '{}'", taskId);
            List<SubTaskResponse> subtasks = subTaskService.getSubTasksByTask(taskId);
            logger.info("getSubTasks: Retrieved {} subtasks for task '{}'", subtasks.size(), taskId);
            return ResponseEntity.ok(subtasks);
        } catch (TaskNotFoundException e) {
            logger.warn("getSubTasks: Task not found '{}'", taskId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("getSubTasks: Unexpected error while fetching subtasks for task '{}'", taskId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unexpected error occurred."));
        }
    }

    /**
     * Delete a subtask by ID.
     */
    @DeleteMapping("/{subTaskId}")
    public ResponseEntity<?> deleteSubTask(@PathVariable UUID subTaskId) {
        try {
            logger.info("deleteSubTask: Deleting subtask '{}'", subTaskId);
            subTaskService.deleteSubTask(subTaskId);
            logger.info("deleteSubTask: Successfully deleted subtask '{}'", subTaskId);
            return ResponseEntity.ok(Map.of("message", "Subtask deleted successfully."));
        } catch (SubTaskNotFoundException e) {
            logger.warn("deleteSubTask: Subtask not found '{}'", subTaskId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("deleteSubTask: Unexpected error while deleting subtask '{}'", subTaskId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unexpected error occurred."));
        }
    }

    /**
     * Retrieve subtasks for a given task.
     */
    @GetMapping("/{subTaskId}")
    public ResponseEntity<?> getSubTaskDetails(@PathVariable UUID subTaskId) {
        try {
            logger.info("getSubTaskDetails: Fetching subtask for details '{}'", subTaskId);
            SubTaskResponse subtask = subTaskService.getSubTask(subTaskId);
            logger.info("getTaskDetails: Task details fetched successfully with ID: {}", subTaskId);
            return ResponseEntity.ok(subtask);
        } catch (TaskNotFoundException e) {
            logger.warn("getSubTaskDetails: subtask not found '{}'", subTaskId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("getSubTaskDetails: Unexpected error while fetching subtasks for task '{}'", subTaskId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unexpected error occurred."));
        }
    }


    /**
     * Update an existing task.
     *
     * @param subTaskId      The task's UUID.
     * @param subTaskRequest DTO with updated task details.
     * @return Updated task.
     */
    @PutMapping("/{subTaskId}")
    public ResponseEntity<SubTaskResponse> updateSubTask(
            @PathVariable UUID subTaskId,
            @Valid @RequestBody SubTaskRequest subTaskRequest) {
        logger.info("updateSubTask: Updating task with ID: {}", subTaskId);
        SubTaskResponse updatedTask = subTaskService.updateSubTask(subTaskId, subTaskRequest);
        logger.info("updateSubTask: Task updated successfully with ID: {}", subTaskId);
        return ResponseEntity.ok(updatedTask);
    }

}
