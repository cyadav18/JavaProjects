package org.guidewire.taskmanager.controller;

import org.guidewire.taskmanager.dto.request.CommentRequest;
import org.guidewire.taskmanager.dto.response.CommentResponse;
import org.guidewire.taskmanager.exceptionhandlers.CommentNotFoundException;
import org.guidewire.taskmanager.exceptionhandlers.TaskNotFoundException;
import org.guidewire.taskmanager.services.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/task-manager/comments")
public class CommentController {

    private final CommentService commentService;
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Add a comment to a task.
     */
    @PostMapping("/task/{taskId}/add")
    public ResponseEntity<?> addCommentToTask(@PathVariable UUID taskId, @RequestBody CommentRequest commentRequest) {
        try {
            logger.info("addCommentToTask: Adding comment for task '{}'", taskId);
            CommentResponse response = commentService.addCommentToTask(taskId, commentRequest);
            logger.info("addCommentToTask: Successfully added comment '{}' for task '{}'", response.getId(), taskId);
            return ResponseEntity.ok(response);
        } catch (TaskNotFoundException e) {
            logger.warn("addCommentToTask: Task not found '{}'", taskId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("addCommentToTask: Unexpected error while adding comment to task '{}'", taskId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unexpected error occurred."));
        }
    }

    /**
     * Add a comment to a subtask.
     */
    @PostMapping("/subtask/{subTaskId}/add")
    public ResponseEntity<?> addCommentToSubTask(@PathVariable UUID subTaskId, @RequestBody CommentRequest commentRequest) {
        try {
            logger.info("addCommentToSubTask: Adding comment for task '{}'", subTaskId);
            CommentResponse response = commentService.addCommentToSubTask(subTaskId, commentRequest);
            logger.info("addCommentToSubTask: Successfully added comment '{}' for task '{}'", response.getId(), subTaskId);
            return ResponseEntity.ok(response);
        } catch (TaskNotFoundException e) {
            logger.warn("addCommentToSubTask: Task not found '{}'", subTaskId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("addCommentToSubTask: Unexpected error while adding comment to task '{}'", subTaskId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unexpected error occurred."));
        }
    }

    /**
     * Retrieve comments for a task.
     */
    @GetMapping("/task/{taskId}/list")
    public ResponseEntity<?> getCommentsForTask(@PathVariable UUID taskId) {
        try {
            logger.info("getComments: Fetching comments for task '{}'", taskId);
            List<CommentResponse> comments = commentService.getCommentsByTask(taskId);
            logger.info("getComments: Retrieved {} comments for task '{}'", comments.size(), taskId);
            return ResponseEntity.ok(comments);
        } catch (TaskNotFoundException e) {
            logger.warn("getComments: Task not found '{}'", taskId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("getComments: Unexpected error while fetching comments for task '{}'", taskId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unexpected error occurred."));
        }
    }

    /**
     * Retrieve comments for a task.
     */
    @GetMapping("/subtask/{subTaskId}/list")
    public ResponseEntity<?> getCommentsForSubTask(@PathVariable UUID subTaskId) {
        try {
            logger.info("getCommentsForSubTask: Fetching comments for task '{}'", subTaskId);
            List<CommentResponse> comments = commentService.getCommentsBySubTask(subTaskId);
            logger.info("getCommentsForSubTask: Retrieved {} comments for task '{}'", comments.size(), subTaskId);
            return ResponseEntity.ok(comments);
        } catch (TaskNotFoundException e) {
            logger.warn("getCommentsForSubTask: Task not found '{}'", subTaskId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("getCommentsForSubTask: Unexpected error while fetching comments for task '{}'", subTaskId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unexpected error occurred."));
        }
    }

    /**
     * Delete a comment by ID.
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable UUID commentId) {
        try {
            logger.info("deleteComment: Deleting comment '{}'", commentId);
            commentService.deleteComment(commentId);
            logger.info("deleteComment: Successfully deleted comment '{}'", commentId);
            return ResponseEntity.ok(Map.of("message", "Comment deleted successfully."));
        } catch (CommentNotFoundException e) {
            logger.warn("deleteComment: Comment not found '{}'", commentId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("deleteComment: Unexpected error while deleting comment '{}'", commentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unexpected error occurred."));
        }
    }
}
