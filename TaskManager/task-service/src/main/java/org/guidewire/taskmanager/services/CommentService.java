package org.guidewire.taskmanager.services;

import org.guidewire.taskmanager.dto.request.CommentRequest;
import org.guidewire.taskmanager.dto.response.CommentResponse;
import org.guidewire.taskmanager.exceptionhandlers.CommentNotFoundException;
import org.guidewire.taskmanager.model.Comment;
import org.guidewire.taskmanager.model.SubTask;
import org.guidewire.taskmanager.model.Task;
import org.guidewire.taskmanager.repository.CommentRepository;
import org.guidewire.taskmanager.repository.SubTaskRepository;
import org.guidewire.taskmanager.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final SubTaskRepository subTaskRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, TaskRepository taskRepository, SubTaskRepository subTaskRepository) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.subTaskRepository = subTaskRepository;
    }

    /**
     * Creates a new comment for either a task or subtask.
     */
    public CommentResponse createComment(CommentRequest request) {
        logger.info("createComment: Creating comment for author: {}", request.getAuthor());

        Comment comment = CommentRequest.convertToEntity(request);

        // Determine whether the comment is for a task or a subtask
        if (request.getTaskId() != null) {
            Task task = taskRepository.findById(request.getTaskId())
                    .orElseThrow(() -> new CommentNotFoundException("Task not found for comment"));
            comment.setTask(task);
        } else if (request.getSubTaskId() != null) {
            SubTask subTask = subTaskRepository.findById(request.getSubTaskId())
                    .orElseThrow(() -> new CommentNotFoundException("SubTask not found for comment"));
            comment.setSubtask(subTask);
        } else {
            throw new IllegalArgumentException("Comment must be associated with either a Task or a SubTask");
        }

        Comment savedComment = commentRepository.save(comment);
        logger.info("createComment: Comment created successfully with ID: {}", savedComment.getId());
        return CommentResponse.convertToDto(savedComment);
    }

    /**
     * Retrieves all comments for a task.
     */
    public List<CommentResponse> getCommentsForTask(UUID taskId) {
        logger.info("getCommentsForTask: Fetching comments for task ID: {}", taskId);
        return commentRepository.findByTaskId(taskId)
                .stream()
                .map(CommentResponse::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all comments for a subtask.
     */
    public List<CommentResponse> getCommentsForSubTask(UUID subTaskId) {
        logger.info("getCommentsForSubTask: Fetching comments for subtask ID: {}", subTaskId);
        return commentRepository.findBySubtaskId(subTaskId)
                .stream()
                .map(CommentResponse::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a comment by ID.
     */
    public void deleteComment(UUID commentId) {
        logger.info("deleteComment: Deleting comment with ID: {}", commentId);
        if (!commentRepository.existsById(commentId)) {
            throw new CommentNotFoundException("Comment not found");
        }
        commentRepository.deleteById(commentId);
        logger.info("deleteComment: Successfully deleted comment with ID: {}", commentId);
    }
}
