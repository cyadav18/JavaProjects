package org.guidewire.taskmanager.controller;

import org.guidewire.taskmanager.exceptionhandlers.AttachmentNotFoundException;
import org.guidewire.taskmanager.model.Attachment;
import org.guidewire.taskmanager.services.AttachmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/task-manager/attachments")

public class AttachmentController {


    private final AttachmentService attachmentService;
    private static final Logger logger = LoggerFactory.getLogger(AttachmentController.class);


    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    /**
     * Upload an attachment for a task.
     */
    @PostMapping("/task/{taskId}/upload")
    public ResponseEntity<?> uploadAttachmentToTask(@PathVariable UUID taskId, @RequestParam("file") MultipartFile file) {
        try {
            logger.info("uploadAttachmentToTask: Received request to upload file '{}' for task ID '{}'", file.getOriginalFilename(), taskId);
            Attachment attachment = attachmentService.uploadAttachmentToTask(taskId, file);
            logger.info("uploadAttachmentToTask: Successfully uploaded attachment '{}' for task '{}'", attachment.getId(), taskId);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("success", "File successfully uploaded"));
        } catch (Exception e) {
            logger.error("uploadAttachmentToTask: Failed to upload attachment for task '{}'", taskId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to upload attachment."));
        }
    }

    /**
     * Upload an attachment for a task.
     */
    @PostMapping("/subtask/{subTaskId}/upload")
    public ResponseEntity<?> uploadAttachmentToSubTask(@PathVariable UUID subTaskId, @RequestParam("file") MultipartFile file) {
        try {
            logger.info("uploadAttachmentToSubTask: Received request to upload file '{}' for task ID '{}'", file.getOriginalFilename(), subTaskId);
            Attachment attachment = attachmentService.uploadAttachmentToTask(subTaskId, file);
            logger.info("uploadAttachmentToSubTask: Successfully uploaded attachment '{}' for task '{}'", attachment.getId(), subTaskId);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("success", "File successfully uploaded"));
        } catch (Exception e) {
            logger.error("uploadAttachmentToSubTask: Failed to upload attachment for task '{}'", subTaskId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to upload attachment."));
        }
    }


    @PostMapping("/task/{taskId}/uploadBase64")
    public ResponseEntity<?> uploadBase64AttachmentToTask(@PathVariable UUID taskId, @RequestBody Map<String, Object> payload) {
        try {
            String base64Data = (String) payload.get("data");
            Map<String, Object> metadata = (Map<String, Object>) payload.get("meta");

            if (base64Data == null || base64Data.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Base64 data is empty"));
            }

            // Remove metadata prefix if present (e.g., "data:image/png;base64,")
            if (base64Data.contains(",")) {
                base64Data = base64Data.split(",")[1];
            }

            // Decode Base64 to binary
            byte[] decodedBytes = Base64.getDecoder().decode(base64Data);

            // Process file upload
            Attachment attachment = attachmentService.uploadBase64AttachmentToTask(taskId, base64Data, metadata);

            return ResponseEntity.ok(Map.of("success", "File successfully uploaded", "attachmentId", attachment.getId()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid Base64 encoding"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "File upload failed"));
        }
    }


    @PostMapping("/subtask/{subTaskId}/uploadBase64")
    public ResponseEntity<?> uploadBase64AttachmentToSubTask(@PathVariable UUID subTaskId, @RequestBody Map<String, Object> payload) {
        try {
            String base64Data = (String) payload.get("data");
            Map<String, Object> metadata = (Map<String, Object>) payload.get("meta");

            if (base64Data == null || base64Data.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Base64 data is empty"));
            }

            // Remove metadata prefix if present (e.g., "data:image/png;base64,")
            if (base64Data.contains(",")) {
                base64Data = base64Data.split(",")[1];
            }

            // Decode Base64 to binary
            byte[] decodedBytes = Base64.getDecoder().decode(base64Data);

            // Process file upload
            Attachment attachment = attachmentService.uploadBase64AttachmentToSubTask(subTaskId, base64Data, metadata);

            return ResponseEntity.ok(Map.of("success", "File successfully uploaded", "attachmentId", attachment.getId()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid Base64 encoding"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "File upload failed"));
        }
    }


    /**
     * Upload an attachment for a comment.
     */
    @PostMapping("/comment/{commentId}/upload")
    public ResponseEntity<?> uploadAttachmentToComment(@PathVariable UUID commentId, @RequestParam("file") MultipartFile file) {
        try {
            logger.info("uploadAttachmentToComment: Received request to upload file '{}' for comment ID '{}'", file.getOriginalFilename(), commentId);
            Attachment attachment = attachmentService.uploadAttachmentToComment(commentId, file);
            logger.info("uploadAttachmentToComment: Successfully uploaded attachment '{}' for comment '{}'", attachment.getId(), commentId);
            return ResponseEntity.ok(attachment);
        } catch (Exception e) {
            logger.error("uploadAttachmentToComment: Failed to upload attachment for comment '{}'", commentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to upload attachment."));
        }
    }

    /**
     * Retrieve all attachments for a specific comment.
     */
    @GetMapping("/comment/{commentId}")
    public ResponseEntity<?> getCommentAttachments(@PathVariable UUID commentId) {
        try {
            logger.info("getCommentAttachments: Fetching attachments for comment ID '{}'", commentId);
            List<String> attachments = attachmentService.getCommentAttachments(commentId);
            logger.info("getCommentAttachments: Retrieved {} attachments for comment '{}'", attachments.size(), commentId);
            return ResponseEntity.ok(attachments);
        } catch (AttachmentNotFoundException e) {
            logger.warn("getCommentAttachments: No attachments found for comment '{}'", commentId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("getCommentAttachments: Unexpected error while fetching attachments for comment '{}'", commentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unexpected error occurred."));
        }
    }

    /**
     * Delete an attachment by ID.
     */
    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<?> deleteAttachment(@PathVariable UUID attachmentId) {
        try {
            logger.info("deleteAttachment: Deleting attachment with ID '{}'", attachmentId);
            attachmentService.deleteAttachment(attachmentId);
            logger.info("deleteAttachment: Successfully deleted attachment '{}'", attachmentId);
            return ResponseEntity.ok(Map.of("message", "Attachment deleted successfully."));
        } catch (AttachmentNotFoundException e) {
            logger.warn("deleteAttachment: Attachment not found with ID '{}'", attachmentId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("deleteAttachment: Unexpected error while deleting attachment '{}'", attachmentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unexpected error occurred."));
        }
    }


    /**
     * Retrieve all attachments for a specific comment.
     */
    @GetMapping("/task/{taskId}")
    public ResponseEntity<?> getTaskAttachments(@PathVariable UUID taskId) {
        try {
            logger.info("getTaskAttachments: Fetching attachments for comment ID '{}'", taskId);
            List<Map<String, Object>> attachments = attachmentService.getTaskAttachments(taskId);
            logger.info("getTaskAttachments: Retrieved {} attachments for comment '{}'", attachments.size(), taskId);
            return ResponseEntity.ok(attachments);
        } catch (AttachmentNotFoundException e) {
            logger.warn("getTaskAttachments: No attachments found for comment '{}'", taskId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("getTaskAttachments: Unexpected error while fetching attachments for comment '{}'", taskId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unexpected error occurred."));
        }
    }

    @GetMapping("/subtask/{subTaskId}")
    public ResponseEntity<?> getSubTaskAttachments(@PathVariable UUID subTaskId) {
        try {
            logger.info("getSubTaskAttachments: Fetching attachments for comment ID '{}'", subTaskId);
            List<Map<String, Object>> attachments = attachmentService.getSubTaskAttachments(subTaskId);
            logger.info("getSubTaskAttachments: Retrieved {} attachments for comment '{}'", attachments.size(), subTaskId);
            return ResponseEntity.ok(attachments);
        } catch (AttachmentNotFoundException e) {
            logger.warn("getSubTaskAttachments: No attachments found for comment '{}'", subTaskId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("getSubTaskAttachments: Unexpected error while fetching attachments for comment '{}'", subTaskId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unexpected error occurred."));
        }
    }
}
