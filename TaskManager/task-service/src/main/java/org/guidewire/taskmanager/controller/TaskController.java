package org.guidewire.taskmanager.controller;

import org.guidewire.taskmanager.dto.request.TaskRequest;
import org.guidewire.taskmanager.dto.response.TaskResponse;
import org.guidewire.taskmanager.services.interfaces.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/task-manager/task")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Fetch all tasks.
     * @return List of all tasks.
     */
    @GetMapping("/list")
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        logger.info("getAllTasks: Fetching all tasks...");
        List<TaskResponse> taskResponses = taskService.getAllTasks();
        logger.info("getAllTasks: Successfully retrieved {} tasks", taskResponses.size());
        return ResponseEntity.ok(taskResponses);
    }

    /**
     * Create a new task.
     * @param taskRequest DTO with task details.
     * @return Created task.
     */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest taskRequest) {
        logger.info("createTask: Received request to create a new task: {}", taskRequest.getSubject());
        TaskResponse taskResponse = taskService.createTask(taskRequest);
        logger.info("createTask: Task created successfully with ID: {}", taskResponse.getId());
        return ResponseEntity.ok(taskResponse);
    }

    /**
     * Update an existing task.
     * @param taskId The task's UUID.
     * @param taskRequest DTO with updated task details.
     * @return Updated task.
     */
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskRequest taskRequest) {
        logger.info("updateTask: Updating task with ID: {}", taskId);
        TaskResponse updatedTask = taskService.updateTask(taskId, taskRequest);
        logger.info("updateTask: Task updated successfully with ID: {}", taskId);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Delete a task by ID.
     * @param taskId The UUID of the task.
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID taskId) {
        logger.info("deleteTask: Received request to delete task with ID: {}", taskId);
        taskService.deleteTask(taskId);
        logger.info("deleteTask: Task deleted successfully with ID: {}", taskId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Gets a task by ID.
     * @param taskId The UUID of the task.
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTaskDetails(@PathVariable UUID taskId) {
        logger.info("getTaskDetails: Received request to fetch task with ID: {}", taskId);
        TaskResponse taskResponse = taskService.getTaskById(taskId);
        logger.info("getTaskDetails: Task details fetched successfully with ID: {}", taskId);
        return ResponseEntity.ok(taskResponse);
    }

}
