package org.guidewire.taskmanager.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.guidewire.taskmanager.model.Enums.Priority;
import org.guidewire.taskmanager.model.SubTask;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class SubTaskRequest {
    @NotBlank(message = "Subject is required")
    @Size(min = 5, max = 100, message = "Subject must be between 5 and 100 characters")
    private String subject;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    private UUID assignee;

    @FutureOrPresent(message = "Due date must be in the future or present")
    private ZonedDateTime dueDate;

    @NotNull(message = "CreatedBy user is required")
    private UUID createdBy;

    @NotNull(message = "Priority is required")
    private Priority priority;

    private List<UUID> watchers;

    public static SubTask convertToEntity(SubTaskRequest request) {
        SubTask subTask = new SubTask();
        subTask.setSubject(request.getSubject());
        subTask.setDescription(request.getDescription());
        if (request.getAssignee() != null) {
            subTask.setAssignee(request.getAssignee());
        }
        subTask.setCreatedBy(request.getCreatedBy());
        subTask.setDueDate(request.getDueDate());
        subTask.setPriority(request.getPriority());
        subTask.setWatchers(request.getWatchers());
        return subTask;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getAssignee() {
        return assignee;
    }

    public void setAssignee(UUID assignee) {
        this.assignee = assignee;
    }

    public ZonedDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(ZonedDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public List<UUID> getWatchers() {
        return watchers;
    }

    public void setWatchers(List<UUID> watchers) {
        this.watchers = watchers;
    }
}
