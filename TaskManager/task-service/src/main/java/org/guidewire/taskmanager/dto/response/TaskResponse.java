package org.guidewire.taskmanager.dto.response;

import lombok.Data;
import org.guidewire.taskmanager.model.Enums.Priority;
import org.guidewire.taskmanager.model.Enums.TaskStatus;
import org.guidewire.taskmanager.model.Task;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class TaskResponse {
    private UUID id;
    private String subject;
    private String description;
    private UUID assignee;
    private UUID createdBy;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private ZonedDateTime dueDate;
    private TaskStatus status;
    private Priority priority;
    private List<UUID> watchers;


    // Convert Task entity to TaskResponseDTO
    public static TaskResponse convertToResponse(Task task) {
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setId(task.getId());
        taskResponse.setSubject(task.getSubject());
        taskResponse.setDescription(task.getDescription());
        taskResponse.setAssignee(task.getAssignee());
        taskResponse.setCreatedAt(task.getCreatedAt());
        taskResponse.setUpdatedAt(task.getUpdatedAt());
        taskResponse.setDueDate(task.getDueDate());
        taskResponse.setStatus(task.getStatus());
        taskResponse.setPriority(task.getPriority());
        taskResponse.setWatchers(task.getWatchers());
        taskResponse.setCreatedBy(task.getCreatedBy());
        return taskResponse;
    }
}
