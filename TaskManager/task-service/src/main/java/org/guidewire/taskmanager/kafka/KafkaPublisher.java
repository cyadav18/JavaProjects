package org.guidewire.taskmanager.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.guidewire.taskmanager.dto.response.TaskResponse;
import org.guidewire.taskmanager.model.SubTask;
import org.guidewire.taskmanager.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaPublisher {

    private static final Logger logger = LoggerFactory.getLogger(KafkaPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topic.task-created}")
    private String taskCreatedTopic;

    @Value("${app.kafka.topic.task-updated}")
    private String taskUpdatedTopic;

    public KafkaPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public void sendTaskCreatedEvent(Task task) {
        try {
            String payload = objectMapper.writeValueAsString(TaskResponse.convertToResponse(task));
            logger.info("Publishing task-created event to topic [{}]: {}", taskCreatedTopic, payload);
            kafkaTemplate.send(taskCreatedTopic, task.getId().toString(), payload);
            kafkaTemplate.flush();
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize task-created event for task ID {}: {}", task.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to serialize task event", e);
        }
    }

    public void sendSubTaskCreatedEvent(SubTask subTask) {
        try {
            String payload = objectMapper.writeValueAsString(subTask);
            logger.info("Publishing subtask-created event to topic [{}]: {}", taskCreatedTopic, payload);
            kafkaTemplate.send(taskCreatedTopic, subTask.getId().toString(), payload);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize subtask-created event for subtask ID {}: {}", subTask.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to serialize subtask event", e);
        }
    }

    public void sendUpdateTaskEvent(Task task) {
        try {
            String payload = objectMapper.writeValueAsString(task);
            logger.info("Publishing task-updated event to topic [{}]: {}", taskUpdatedTopic, payload);
            kafkaTemplate.send(taskUpdatedTopic, task.getId().toString(), payload);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize task-updated event for task ID {}: {}", task.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to serialize task event", e);
        }
    }

    public void sendUpdateSubTaskEvent(SubTask subTask) {
        try {
            String payload = objectMapper.writeValueAsString(subTask);
            logger.info("Publishing subtask-updated event to topic [{}]: {}", taskUpdatedTopic, payload);
            kafkaTemplate.send(taskUpdatedTopic, subTask.getId().toString(), payload);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize subtask-updated event for subtask ID {}: {}", subTask.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to serialize subtask event", e);
        }
    }
}
