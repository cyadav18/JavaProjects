package org.guidewire.taskmanager.dto.response;

import org.guidewire.taskmanager.model.Comment;

import java.util.UUID;


public class CommentResponse {
    private UUID id;
    private UUID author;
    private String content;

    public static CommentResponse convertToDto(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setAuthor(comment.getAuthor());
        response.setContent(comment.getContent());
        return response;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
}
