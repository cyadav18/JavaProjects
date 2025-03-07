package org.guidewire.taskmanager.exceptionhandlers;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String message) {
        super(message);
    }
}
