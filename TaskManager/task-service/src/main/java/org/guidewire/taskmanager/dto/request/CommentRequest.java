package org.guidewire.taskmanager.dto.request;

import lombok.Data;
import org.guidewire.taskmanager.model.Comment;

import java.util.UUID;

@Data
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
}
