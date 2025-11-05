package org.guidewire.taskmanager.exceptionhandlers;

public class FileUploadFailedException extends RuntimeException {
    public FileUploadFailedException(String message) {
        super(message);
    }
}
