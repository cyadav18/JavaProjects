package org.guidewire.taskmanager.services;

import lombok.extern.slf4j.Slf4j;
import org.guidewire.taskmanager.dto.request.CommentRequest;
import org.guidewire.taskmanager.dto.response.CommentResponse;
import org.guidewire.taskmanager.exceptionhandlers.CommentNotFoundException;
import org.guidewire.taskmanager.model.Comment;
import org.guidewire.taskmanager.model.Task;
import org.guidewire.taskmanager.repository.CommentRepository;
import org.guidewire.taskmanager.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    public CommentService(CommentRepository commentRepository, TaskRepository taskRepository) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
    }

    public CommentResponse addCommentToTask(UUID taskId, CommentRequest commentRequest) {
        logger.info("addCommentToTask: Adding comment to task '{}'", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        Comment comment = CommentRequest.convertToEntity(commentRequest);
        comment.setTask(task);
        comment = commentRepository.save(comment);

        logger.info("addCommentToTask: Comment '{}' added successfully", comment.getId());
        return CommentResponse.convertToDto(comment);
    }

    public List<CommentResponse> getCommentsByTask(UUID taskId) {
        logger.info("getCommentsByTask: Fetching comments for task '{}'", taskId);
        return commentRepository.findByTaskId(taskId)
                .stream()
                .map(CommentResponse::convertToDto)
                .collect(Collectors.toList());
    }

    public void deleteComment(UUID commentId) {
        logger.info("deleteComment: Deleting comment '{}'", commentId);
        if (!commentRepository.existsById(commentId)) {
            throw new CommentNotFoundException("Comment not found with ID: " + commentId);
        }
        commentRepository.deleteById(commentId);
        logger.info("deleteComment: Comment '{}' deleted successfully", commentId);
    }
}
