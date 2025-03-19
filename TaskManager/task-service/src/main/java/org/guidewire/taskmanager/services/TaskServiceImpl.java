package org.guidewire.taskmanager.services;

import org.guidewire.taskmanager.dto.request.TaskRequest;
import org.guidewire.taskmanager.dto.response.TaskResponse;
import org.guidewire.taskmanager.exceptionhandlers.TaskNotFoundException;
import org.guidewire.taskmanager.model.Task;
import org.guidewire.taskmanager.repository.TaskRepository;
import org.guidewire.taskmanager.services.interfaces.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
    private final TaskRepository taskRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Creates a new task.
     *
     * @param taskRequest Request DTO containing task details.
     * @return TaskResponse DTO of the saved task.
     */
    @Override
    public TaskResponse createTask(TaskRequest taskRequest) {
        logger.info("createTask: Creating a new task with subject: {}", taskRequest.getSubject());

        Task task = TaskRequest.convertToEntity(taskRequest);
        Task savedTask = taskRepository.save(task);

        logger.info("Task created successfully with ID: {}", savedTask.getId());
        return TaskResponse.convertToResponse(savedTask);
    }

    /**
     * Retrieves a task by ID.
     *
     * @param taskId The UUID of the task.
     * @return Optional TaskResponse DTO.
     */
    @Override
    public TaskResponse getTaskById(UUID taskId) {
        logger.info("getTaskById: Fetching task with ID: {}", taskId);

        Optional<Task> task = taskRepository.findById(taskId);
        if (!task.isPresent()) {
            throw new TaskNotFoundException("Task with ID: " + taskId + " not found");
        }
        return TaskResponse.convertToResponse(task.get());
    }

    /**
     * Updates an existing task.
     *
     * @param taskId      Task ID to update.
     * @param taskRequest DTO with new task details.
     * @return Updated TaskResponse DTO.
     */
    @Override
    public TaskResponse updateTask(UUID taskId, TaskRequest taskRequest) {
        logger.info("updateTask: Updating task with ID: {}", taskId);

        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    logger.warn("updateTask: Task with ID {} not found", taskId);
                    return new TaskNotFoundException("Task with ID " + taskId + " not found");
                });

        // Update fields
        existingTask.setSubject(taskRequest.getSubject());
        existingTask.setAssignee(taskRequest.getAssignee());
        existingTask.setDescription(taskRequest.getDescription());
        existingTask.setDueDate(taskRequest.getDueDate());
        existingTask.setPriority(taskRequest.getPriority());
        existingTask.setWatchers(taskRequest.getWatchers());

        Task updatedTask = taskRepository.save(existingTask);
        logger.info("updateTask: Task with ID {} updated successfully", taskId);

        return TaskResponse.convertToResponse(updatedTask);
    }

    /**
     * Deletes a task by ID.
     *
     * @param taskId The UUID of the task.
     */
    @Override
    public void deleteTask(UUID taskId) {
        logger.info("deleteTask: Deleting task with ID: {}", taskId);

        if (!taskRepository.existsById(taskId)) {
            logger.warn("deleteTask: Task with ID {} not found", taskId);
            throw new TaskNotFoundException("Task with ID " + taskId + " not found");
        }

        taskRepository.deleteById(taskId);
        logger.info("deleteTask: Task with ID {} deleted successfully", taskId);
    }

    /**
     * Retrieves all tasks.
     *
     * @return List of TaskResponse DTOs.
     */
    @Override
    public List<TaskResponse> getAllTasks() {
        logger.info("getAllTasks: Fetching all tasks");

        List<TaskResponse> taskDTOs = taskRepository.findAll().stream()
                .map(TaskResponse::convertToResponse)
                .collect(Collectors.toList());

//        if (taskDTOs.isEmpty()) {
//            throw new NoTasksFoundException("No tasks available.");
//        }

        logger.info("getAllTasks: Fetched {} tasks", taskDTOs.size());
        return taskDTOs;
    }
}
