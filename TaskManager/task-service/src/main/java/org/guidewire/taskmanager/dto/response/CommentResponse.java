package org.guidewire.taskmanager.dto.response;

import lombok.Data;
import org.guidewire.taskmanager.model.Comment;

import java.util.UUID;

@Data
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
}
