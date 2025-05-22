package org.guidewire.taskmanager.dto.request;

import org.guidewire.taskmanager.model.Comment;

import java.util.UUID;

public class CommentRequest {
    private UUID author;
    private String content;
    private UUID taskId;
    private UUID subTaskId;

    public static Comment convertToEntity(CommentRequest request) {
        Comment comment = new Comment();
        comment.setAuthor(request.getAuthor());
        comment.setContent(request.getContent());
        return comment;
    }

    public UUID getAuthor() {
        return author;
    }

    public void setAuthor(UUID author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public UUID getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(UUID subTaskId) {
        this.subTaskId = subTaskId;
    }
}
