package org.guidewire.taskmanager.services;

import lombok.extern.slf4j.Slf4j;
import org.guidewire.taskmanager.dto.request.SubTaskRequest;
import org.guidewire.taskmanager.dto.request.TaskRequest;
import org.guidewire.taskmanager.dto.response.SubTaskResponse;
import org.guidewire.taskmanager.dto.response.TaskResponse;
import org.guidewire.taskmanager.exceptionhandlers.SubTaskNotFoundException;
import org.guidewire.taskmanager.exceptionhandlers.TaskNotFoundException;
import org.guidewire.taskmanager.model.SubTask;
import org.guidewire.taskmanager.model.Task;
import org.guidewire.taskmanager.repository.SubTaskRepository;
import org.guidewire.taskmanager.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubTaskService {

    private final SubTaskRepository subTaskRepository;
    private final TaskRepository taskRepository;
    private static final Logger logger = LoggerFactory.getLogger(SubTaskService.class);

    public SubTaskService(SubTaskRepository subTaskRepository, TaskRepository taskRepository) {
        this.subTaskRepository = subTaskRepository;
        this.taskRepository = taskRepository;
    }

    public SubTaskResponse createSubTask(UUID taskId, SubTaskRequest subTaskRequest) {
        logger.info("createSubTask: Creating subtask for task ID '{}'", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        SubTask subTask = SubTaskRequest.convertToEntity(subTaskRequest);
        subTask.setTask(task);
        subTask = subTaskRepository.save(subTask);

        logger.info("createSubTask: Subtask '{}' created successfully for task '{}'", subTask.getId(), taskId);
        return SubTaskResponse.convertToDto(subTask);
    }

    public List<SubTaskResponse> getSubTasksByTask(UUID taskId) {
        logger.info("getSubTasksByTask: Retrieving subtasks for task '{}'", taskId);
        return subTaskRepository.findByTaskId(taskId)
                .stream()
                .map(SubTaskResponse::convertToDto)
                .collect(Collectors.toList());
    }

    public void deleteSubTask(UUID subTaskId) {
        logger.info("deleteSubTask: Deleting subtask '{}'", subTaskId);
        if (!subTaskRepository.existsById(subTaskId)) {
            throw new SubTaskNotFoundException("Subtask not found with ID: " + subTaskId);
        }
        subTaskRepository.deleteById(subTaskId);
        logger.info("deleteSubTask: Subtask '{}' deleted successfully", subTaskId);
    }

    public SubTaskResponse getSubTask(UUID subTaskId) {
        logger.info("getSubTask: getting subtask '{}'", subTaskId);
        SubTask subTask = subTaskRepository.findById(subTaskId).orElseThrow(() -> new SubTaskNotFoundException("Subtask not found with ID: " + subTaskId));
        SubTaskResponse subTaskResponse = SubTaskResponse.convertToDto(subTask);
        logger.info("getSubTask: Subtask '{}' deleted successfully", subTaskId);
        return subTaskResponse;
    }

    /**
     * Updates an existing task.
     *
     * @param subTaskId      Task ID to update.
     * @param subTaskRequest DTO with new task details.
     * @return Updated TaskResponse DTO.
     */
    public SubTaskResponse updateSubTask(UUID subTaskId, SubTaskRequest subTaskRequest) {
        logger.info("updateSubTask: Updating subtask with ID: {}", subTaskId);

        SubTask existingTask = subTaskRepository.findById(subTaskId)
                .orElseThrow(() -> {
                    logger.warn("updateTask: subTask with ID {} not found", subTaskId);
                    return new SubTaskNotFoundException("SubTask with ID " + subTaskId + " not found");
                });

        // Update fields
        existingTask.setSubject(subTaskRequest.getSubject());
        existingTask.setAssignee(subTaskRequest.getAssignee());
        existingTask.setDescription(subTaskRequest.getDescription());
        existingTask.setDueDate(subTaskRequest.getDueDate());
        existingTask.setPriority(subTaskRequest.getPriority());
        existingTask.setWatchers(subTaskRequest.getWatchers());

        SubTask updatedTask = subTaskRepository.save(existingTask);
        logger.info("updateTask: Task with ID {} updated successfully", subTaskId);

        return SubTaskResponse.convertToDto(updatedTask);
    }

}
