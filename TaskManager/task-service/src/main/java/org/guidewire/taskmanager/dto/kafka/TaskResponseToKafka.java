package org.guidewire.taskmanager.dto.kafka;

import org.guidewire.taskmanager.model.Enums.Priority;
import org.guidewire.taskmanager.model.Enums.TaskStatus;
import org.guidewire.taskmanager.model.SubTask;
import org.guidewire.taskmanager.model.Task;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class TaskResponseToKafka {
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
    private List<SubTaskResponseToKafka> subTasks;

    // Convert Task entity to TaskResponseDTO
    public static TaskResponseToKafka convertToResponse(Task task) {
        TaskResponseToKafka taskResponse = new TaskResponseToKafka();
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
        List<SubTaskResponseToKafka> subTaskResponseToKafkaList = new ArrayList<SubTaskResponseToKafka>();
        for (SubTask subTask : task.getSubTasks()) {
            subTaskResponseToKafkaList.add(SubTaskResponseToKafka.convertToDto(subTask));
        }
        taskResponse.setSubTasks(subTaskResponseToKafkaList);
        return taskResponse;
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

    public UUID getAssignee() {
        return assignee;
    }

    public void setAssignee(UUID assignee) {
        this.assignee = assignee;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
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

    public List<SubTaskResponseToKafka> getSubTasks() {
        return subTasks;
    }
    public void setSubTasks(List<SubTaskResponseToKafka> subTasks) {
        this.subTasks = subTasks;
    }
}
