package org.guidewire.taskmanager.dto.kafka;

import org.guidewire.taskmanager.model.Enums.Priority;
import org.guidewire.taskmanager.model.Enums.TaskStatus;
import org.guidewire.taskmanager.model.SubTask;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class SubTaskResponseToKafka {
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

    public static SubTaskResponseToKafka convertToDto(SubTask subTask) {
        SubTaskResponseToKafka response = new SubTaskResponseToKafka();
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public UUID getAssignee() {
        return assignee;
    }

    public void setAssignee(UUID assignee) {
        this.assignee = assignee;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ZonedDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(ZonedDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
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
