package org.guidewire.taskmanager.dto.request;

import lombok.Data;
import org.guidewire.taskmanager.model.Enums.Priority;
import org.guidewire.taskmanager.model.Enums.TaskStatus;
import org.guidewire.taskmanager.model.SubTask;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class SubTaskRequest {
    private String subject;
    private String description;
    private UUID createdBy;
    private UUID assignee;
    private ZonedDateTime dueDate;
    private TaskStatus status;
    private Priority priority;
    private UUID taskId;

    public static SubTask convertToEntity(SubTaskRequest request) {
        SubTask subTask = new SubTask();
        subTask.setSubject(request.getSubject());
        subTask.setDescription(request.getDescription());
        subTask.setCreatedBy(request.getCreatedBy());
        subTask.setAssignee(request.getAssignee());
        subTask.setDueDate(request.getDueDate());
        subTask.setStatus(request.getStatus());
        subTask.setPriority(request.getPriority());
        return subTask;
    }
}
