package org.guidewire.taskmanager.dto.response;

import lombok.Data;
import org.guidewire.taskmanager.model.Enums.Priority;
import org.guidewire.taskmanager.model.Enums.TaskStatus;
import org.guidewire.taskmanager.model.SubTask;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class SubTaskResponse {
    private UUID id;
    private String subject;
    private String description;
    private UUID createdBy;
    private UUID assignee;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private ZonedDateTime dueDate;
    private TaskStatus status;
    private Priority priority;
    private List<UUID> watchers;

    public static SubTaskResponse convertToDto(SubTask subTask) {
        SubTaskResponse response = new SubTaskResponse();
        response.setId(subTask.getId());
        response.setSubject(subTask.getSubject());
        response.setDescription(subTask.getDescription());
        response.setAssignee(subTask.getAssignee());
        response.setCreatedAt(subTask.getCreatedAt());
        response.setUpdatedAt(subTask.getUpdatedAt());
        response.setDueDate(subTask.getDueDate());
        response.setStatus(subTask.getStatus());
        response.setPriority(subTask.getPriority());
        response.setWatchers(subTask.getWatchers());
        response.setCreatedBy(subTask.getCreatedBy());
        return response;
    }
}
