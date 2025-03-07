package org.guidewire.taskmanager.dto.response;

import lombok.Data;
import org.guidewire.taskmanager.model.Enums.Priority;
import org.guidewire.taskmanager.model.Enums.TaskStatus;
import org.guidewire.taskmanager.model.SubTask;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class SubTaskResponse {
    private UUID id;
    private String subject;
    private String description;
    private UUID createdBy;
    private UUID assignee;
    private ZonedDateTime dueDate;
    private TaskStatus status;
    private Priority priority;

    public static SubTaskResponse convertToDto(SubTask subTask) {
        SubTaskResponse response = new SubTaskResponse();
        response.setId(subTask.getId());
        response.setSubject(subTask.getSubject());
        response.setDescription(subTask.getDescription());
        response.setCreatedBy(subTask.getCreatedBy());
        response.setAssignee(subTask.getAssignee());
        response.setDueDate(subTask.getDueDate());
        response.setStatus(subTask.getStatus());
        response.setPriority(subTask.getPriority());
        return response;
    }
}
