package org.guidewire.taskmanager.services;

import org.guidewire.taskmanager.dto.request.SubTaskRequest;
import org.guidewire.taskmanager.dto.response.SubTaskResponse;
import org.guidewire.taskmanager.exceptionhandlers.SubTaskNotFoundException;
import org.guidewire.taskmanager.model.SubTask;
import org.guidewire.taskmanager.repository.SubTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubTaskService {
    private static final Logger logger = LoggerFactory.getLogger(SubTaskService.class);
    private final SubTaskRepository subTaskRepository;

    @Autowired
    public SubTaskService(SubTaskRepository subTaskRepository) {
        this.subTaskRepository = subTaskRepository;
    }

    public SubTaskResponse createSubTask(SubTaskRequest request) {
        logger.info("createSubTask: Creating a new subtask for task {}", request.getTaskId());
        SubTask subTask = SubTaskRequest.convertToEntity(request);
        SubTask savedSubTask = subTaskRepository.save(subTask);
        return SubTaskResponse.convertToDto(savedSubTask);
    }

    public List<SubTaskResponse> getSubTasksByTaskId(UUID taskId) {
        logger.info("getSubTasksByTaskId: Fetching subtasks for task {}", taskId);
        return subTaskRepository.findByTaskId(taskId)
                .stream()
                .map(SubTaskResponse::convertToDto)
                .collect(Collectors.toList());
    }

    public void deleteSubTask(UUID subTaskId) {
        logger.info("deleteSubTask: Deleting subtask {}", subTaskId);
        if (!subTaskRepository.existsById(subTaskId)) {
            throw new SubTaskNotFoundException("Subtask not found with ID: " + subTaskId);
        }
        subTaskRepository.deleteById(subTaskId);
    }
}
