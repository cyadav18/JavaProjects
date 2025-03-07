package org.guidewire.taskmanager.repository;

import org.guidewire.taskmanager.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByTaskId(UUID taskId);
    List<Comment> findBySubtaskId(UUID subTaskId);
}
