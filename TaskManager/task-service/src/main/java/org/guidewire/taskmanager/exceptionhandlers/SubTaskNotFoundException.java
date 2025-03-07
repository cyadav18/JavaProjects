package org.guidewire.taskmanager.exceptionhandlers;

public class SubTaskNotFoundException extends RuntimeException {
    public SubTaskNotFoundException(String message) {
        super(message);
    }
}
