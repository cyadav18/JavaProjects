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

    public CommentService(CommentRepository commentRepository, TaskRepository taskRepository, SubTaskRepository subTaskRepository) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.subTaskRepository = subTaskRepository;
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


    public CommentResponse addCommentToSubTask(UUID subTaskId, CommentRequest commentRequest) {
        logger.info("addCommentToSubTask: Adding comment to task '{}'", subTaskId);

        SubTask task = subTaskRepository.findById(subTaskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + subTaskId));

        Comment comment = CommentRequest.convertToEntity(commentRequest);
        comment.setSubtask(task);
        comment = commentRepository.save(comment);

        logger.info("addCommentToSubTask: Comment '{}' added successfully", comment.getId());
        return CommentResponse.convertToDto(comment);
    }

    public List<CommentResponse> getCommentsByTask(UUID taskId) {
        logger.info("getCommentsByTask: Fetching comments for task '{}'", taskId);
        List<Comment> comment = commentRepository.findByTaskId(taskId);
        logger.info("getCommentsByTask: Retrieved comments for task '{}'", comment.size());
        return comment.stream()
                .map(CommentResponse::convertToDto)
                .collect(Collectors.toList());

    }

    public List<CommentResponse> getCommentsBySubTask(UUID subTaskId) {
        logger.info("getCommentsBySubTask: Fetching comments for task '{}'", subTaskId);
        return commentRepository.findBySubtaskId(subTaskId)
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
