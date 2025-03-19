package org.guidewire.taskmanager.services.interfaces;

import org.guidewire.taskmanager.dto.request.TaskRequest;
import org.guidewire.taskmanager.dto.response.TaskResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskService {
    TaskResponse createTask(TaskRequest taskRequest);
    TaskResponse getTaskById(UUID taskId);
    TaskResponse updateTask(UUID taskId, TaskRequest taskRequest);
    void deleteTask(UUID taskId);
    List<TaskResponse> getAllTasks();
}