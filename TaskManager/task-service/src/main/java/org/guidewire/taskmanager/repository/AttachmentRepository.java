package org.guidewire.taskmanager.repository;

import org.guidewire.taskmanager.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
    List<Attachment> findByTaskId(UUID taskId);

    List<Attachment> findBySubTaskId(UUID subTaskId);

    List<Attachment> findByCommentId(UUID commentId);
}
