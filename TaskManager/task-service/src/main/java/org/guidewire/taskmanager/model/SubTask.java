package org.guidewire.taskmanager.model;

import jakarta.persistence.*;
import lombok.*;
import org.guidewire.taskmanager.model.Enums.Priority;
import org.guidewire.taskmanager.model.Enums.TaskStatus;

import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "subtasks")
public class SubTask extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private UUID createdBy;

    private UUID assignee;

    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(nullable = false)
    private ZonedDateTime updatedAt;

    private ZonedDateTime dueDate;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "subTask_watchers", joinColumns = @JoinColumn(name = "subTask_id"))
    @Column(name = "watcher_id")
    private List<UUID> watchers;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "task_id")
    private Task task;

    @OneToMany(mappedBy = "subTask", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments;

    @OneToMany(mappedBy = "subtask", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;


    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now(ZoneOffset.UTC);
        this.updatedAt = ZonedDateTime.now(ZoneOffset.UTC);
        this.status = TaskStatus.NOT_STARTED;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = ZonedDateTime.now(ZoneOffset.UTC);
    }
}
