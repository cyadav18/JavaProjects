package org.guidewire.taskmanager.exceptionhandlers;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(String message) {
        super(message);
    }
}