package org.guidewire.taskmanager.exceptionhandlers;

public class NoTasksFoundException extends RuntimeException {
    public NoTasksFoundException(String message) {
        super(message);
    }
}
