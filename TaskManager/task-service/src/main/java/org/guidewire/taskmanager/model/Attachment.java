package org.guidewire.taskmanager.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "attachments")
public class Attachment extends BaseEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String filePath;  // Local file path where it's stored

    @Column(nullable = false)
    private Long size; // File size in bytes


    private ZonedDateTime created;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)  
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)  
    @JoinColumn(name = "comment_id", referencedColumnName = "id")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)  
    @JoinColumn(name = "subtask_id", referencedColumnName = "id")
    private SubTask subTask;

    @PrePersist
    public void prePersist() {
        created = ZonedDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {

    }

}
