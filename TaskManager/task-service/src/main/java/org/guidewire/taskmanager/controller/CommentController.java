package org.guidewire.taskmanager.controller;

import org.guidewire.taskmanager.dto.request.CommentRequest;
import org.guidewire.taskmanager.dto.response.CommentResponse;
import org.guidewire.taskmanager.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.createComment(request));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<CommentResponse>> getCommentsForTask(@PathVariable UUID taskId) {
        return ResponseEntity.ok(commentService.getCommentsForTask(taskId));
    }

    @GetMapping("/subtask/{subTaskId}")
    public ResponseEntity<List<CommentResponse>> getCommentsForSubTask(@PathVariable UUID subTaskId) {
        return ResponseEntity.ok(commentService.getCommentsForSubTask(subTaskId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
