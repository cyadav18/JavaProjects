package org.guidewire.taskmanager.services;

import org.guidewire.taskmanager.exceptionhandlers.AttachmentNotFoundException;
import org.guidewire.taskmanager.exceptionhandlers.CommentNotFoundException;
import org.guidewire.taskmanager.exceptionhandlers.FileStorageException;
import org.guidewire.taskmanager.exceptionhandlers.TaskNotFoundException;
import org.guidewire.taskmanager.model.Attachment;
import org.guidewire.taskmanager.model.Comment;
import org.guidewire.taskmanager.model.SubTask;
import org.guidewire.taskmanager.model.Task;
import org.guidewire.taskmanager.repository.AttachmentRepository;
import org.guidewire.taskmanager.repository.CommentRepository;
import org.guidewire.taskmanager.repository.SubTaskRepository;
import org.guidewire.taskmanager.repository.TaskRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final SubTaskRepository subTaskRepository;
    private final CommentRepository commentRepository;

    @Value("${file.storage.local.path}")
    private String baseStoragePath;

    @Value("${attachments.storage.tasks-path}")
    private String taskStoragePath;

    @Value("${attachments.storage.comments-path}")
    private String commentStoragePath;

    @Value("${attachments.storage.subtasks-path}")
    private String subTaskStoragePath;

    private static final Logger logger = LoggerFactory.getLogger(AttachmentService.class);

    public AttachmentService(AttachmentRepository attachmentRepository, TaskRepository taskRepository, SubTaskRepository subTaskRepository, CommentRepository commentRepository) {
        this.attachmentRepository = attachmentRepository;
        this.taskRepository = taskRepository;
        this.subTaskRepository = subTaskRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * Uploads an attachment for a task.
     */
    public Attachment uploadAttachmentToTask(UUID taskId, MultipartFile file) {
        logger.info("uploadAttachmentToTask: Uploading file '{}' for task ID '{}'", file.getOriginalFilename(), taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + taskId));

        return storeFile(taskStoragePath, file, task, null, null);
    }

    /**
     * Uploads an attachment for a task.
     */
    public Attachment uploadAttachmentToSubTask(UUID subTaskId, MultipartFile file) {
        logger.info("uploadAttachmentToSubTask: Uploading file '{}' for task ID '{}'", file.getOriginalFilename(), subTaskId);

        SubTask subTask = subTaskRepository.findById(subTaskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + subTaskId));

        return storeFile(taskStoragePath, file, null, null, subTask);
    }


    /**
     * Uploads an attachment for a task via Base64
     */
    public Attachment uploadBase64AttachmentToTask(UUID taskId, String base64Data, Map<String, Object> metadata) {
        logger.info("uploadBase64AttachmentToTask: Uploading Base64 file for task '{}'", taskId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + taskId));

        return storeFileFromBase64(taskStoragePath, base64Data, metadata, task, null, null);
    }

    /**
     * Uploads an attachment for a task via Base64
     */
    public Attachment uploadBase64AttachmentToSubTask(UUID subTaskId, String base64Data, Map<String, Object> metadata) {
        logger.info("uploadBase64AttachmentToSubTask: Uploading Base64 file for task '{}'", subTaskId);
        SubTask subTask = subTaskRepository.findById(subTaskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + subTaskId));

        return storeFileFromBase64(taskStoragePath, base64Data, metadata, null, null, subTask);
    }

    /**
     * Uploads an attachment for a comment.
     */
    public Attachment uploadAttachmentToComment(UUID commentId, MultipartFile file) {
        logger.info("uploadAttachmentToComment: Uploading file '{}' for comment ID '{}'", file.getOriginalFilename(), commentId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with ID: " + commentId));

        return storeFile(commentStoragePath, file, null, comment, null);
    }

    /**
     * Uploads an attachment for a comment via Base64
     */
    public Attachment uploadBase64AttachmentToComment(UUID commentId, String base64Data, Map<String, Object> metadata) {
        logger.info("uploadBase64AttachmentToComment: Uploading Base64 file for comment '{}'", commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with ID: " + commentId));

        return storeFileFromBase64(commentStoragePath, base64Data, metadata, null, comment, null);
    }


    /**
     * Handles file storage logic.
     */
    private @NotNull Attachment storeFile(String storagePath, MultipartFile file, Task task, Comment comment, SubTask subTask) {
        // Ensure a unique directory per attachment
        String directoryPath = Paths.get(storagePath, UUID.randomUUID().toString()).toString();
        File directory = new File(directoryPath);
        if (!directory.exists() && !directory.mkdirs()) {
            logger.error("storeFile: Failed to create directory '{}'", directoryPath);
            throw new FileStorageException("Failed to create storage directory.");
        }

        // Extract file extension safely
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";

        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        } else {
            logger.warn("storeFile: No file extension found for '{}'. Assigning default '.bin'", originalFileName);
            fileExtension = ".bin"; // Assigning default unknown file types
        }

        // Generate a new filename while preserving the extension
        String fileName = UUID.randomUUID() + fileExtension;
        Path filePath = Paths.get(directoryPath, fileName);

        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("storeFile: File '{}' successfully stored at '{}'", originalFileName, filePath);
        } catch (IOException e) {
            logger.error("storeFile: Error storing file '{}'", originalFileName, e);
            throw new FileStorageException("Could not store file: " + originalFileName);
        }

        // Save file metadata to database
        Attachment attachment = new Attachment();
        attachment.setName(originalFileName);
        attachment.setType(file.getContentType());
        attachment.setFilePath(filePath.toString());
        attachment.setSize(file.getSize());
        attachment.setTask(task);
        attachment.setComment(comment);
        attachment.setSubTask(subTask);

        return attachmentRepository.save(attachment);
    }


    public Attachment storeFileFromBase64(String storagePath, String base64Data, Map<String, Object> metadata, Task task, Comment comment, SubTask subTask) {
        try {
            // Validate metadata
            if (!metadata.containsKey("name") || !metadata.containsKey("type") || !metadata.containsKey("size")) {
                throw new IllegalArgumentException("Missing required metadata: 'name', 'type', or 'size'");
            }

            String originalFileName = (String) metadata.get("name");
            String fileType = (String) metadata.get("type");
            Long fileSize = metadata.get("size") != null ? Long.parseLong(metadata.get("size").toString()) : 0L;

            // Extract file extension
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            } else {
                logger.warn("storeFileFromBase64: No file extension found for '{}'. Assigning default '.bin'", originalFileName);
                fileExtension = ".bin"; // Fallback for unknown file types
            }

            // Generate a unique directory path
            String directoryPath = Paths.get(storagePath, UUID.randomUUID().toString()).toString();
            File directory = new File(directoryPath);
            if (!directory.exists() && !directory.mkdirs()) {
                logger.error("storeFileFromBase64: Failed to create directory '{}'", directoryPath);
                throw new FileStorageException("Failed to create storage directory.");
            }

            // Generate unique filename while preserving extension
            String fileName = UUID.randomUUID() + fileExtension;
            Path filePath = Paths.get(directoryPath, fileName);

            // Validate Base64 string before decoding
            if (base64Data == null || base64Data.isBlank()) {
                throw new IllegalArgumentException("Base64 data is empty or null");
            }

            // Decode Base64 and store the file
            byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
            try (OutputStream outputStream = new FileOutputStream(filePath.toFile())) {
                outputStream.write(decodedBytes);
            }

            logger.info("storeFileFromBase64: Successfully stored file '{}' at '{}'", originalFileName, filePath);

            // Save file metadata to database
            Attachment attachment = new Attachment();
            attachment.setName(originalFileName);
            attachment.setType(fileType);
            attachment.setFilePath(filePath.toString());
            attachment.setSize(fileSize);
            attachment.setTask(task);
            attachment.setComment(comment);
            attachment.setSubTask(subTask);

            return attachmentRepository.save(attachment);
        } catch (IOException e) {
            logger.error("storeFileFromBase64: Error storing Base64 file '{}'", metadata.get("name"), e);
            throw new FileStorageException("Could not store Base64 file: " + metadata.get("name"));
        }
    }


    /**
     * Retrieves attachments for a specific task.
     */
    public List<Map<String, Object>> getTaskAttachments(UUID taskId) {
        logger.info("getTaskAttachments: Retrieving attachments for task ID '{}'", taskId);

        List<Attachment> attachments = attachmentRepository.findByTaskId(taskId);

        if (attachments.isEmpty()) {
            logger.warn("getTaskAttachments: No attachments found for task ID '{}'", taskId);
            return List.of();
        }

        return attachments.stream()
                .map(attachment -> {
                    Map<String, Object> attachmentInfo = new HashMap<>();
                    attachmentInfo.put("id", attachment.getId());
                    attachmentInfo.put("fileName", attachment.getName());
                    attachmentInfo.put("filePath", Paths.get(attachment.getFilePath()).toString());
                    attachmentInfo.put("fileType", attachment.getType());
                    attachmentInfo.put("fileSize", attachment.getSize());

                    return attachmentInfo; // Ensuring Map<String, Object>
                })
                .collect(Collectors.toList());
    }


    /**
     * Retrieves attachments for a specific task.
     */
    public List<Map<String, Object>> getSubTaskAttachments(UUID subTaskId) {
        logger.info("getSubTaskAttachments: Retrieving attachments for task ID '{}'", subTaskId);

        List<Attachment> attachments = attachmentRepository.findBySubTaskId(subTaskId);

        if (attachments.isEmpty()) {
            logger.warn("getSubTaskAttachments: No attachments found for task ID '{}'", subTaskId);
            return List.of();
        }

        return attachments.stream()
                .map(attachment -> {
                    Map<String, Object> attachmentInfo = new HashMap<>();
                    attachmentInfo.put("id", attachment.getId());
                    attachmentInfo.put("fileName", attachment.getName());
                    attachmentInfo.put("filePath", Paths.get(attachment.getFilePath()).toString());
                    attachmentInfo.put("fileType", attachment.getType());
                    attachmentInfo.put("fileSize", attachment.getSize());

                    return attachmentInfo; // Ensuring Map<String, Object>
                })
                .collect(Collectors.toList());
    }




    /**
     * Retrieves attachments for a specific comment.
     */
    public List<String> getCommentAttachments(UUID commentId) {
        logger.info("getCommentAttachments: Retrieving attachments for comment ID '{}'", commentId);
        return attachmentRepository.findByCommentId(commentId)
                .stream()
                .map(Attachment::getFilePath)
                .collect(Collectors.toList());
    }

    /**
     * Deletes an attachment.
     */
    public void deleteAttachment(UUID attachmentId) {
        logger.info("deleteAttachment: Deleting attachment with ID '{}'", attachmentId);

        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new AttachmentNotFoundException("Attachment not found with ID: " + attachmentId));

        try {
            Files.deleteIfExists(Paths.get(attachment.getFilePath()));
            attachmentRepository.deleteById(attachmentId);
            logger.info("deleteAttachment: Successfully deleted attachment '{}'", attachment.getName());
        } catch (IOException e) {
            logger.error("deleteAttachment: Failed to delete file '{}'", attachment.getFilePath(), e);
            throw new FileStorageException("Could not delete file: " + attachment.getName());
        }
    }


}
