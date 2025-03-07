package org.guidewire.taskmanager.controller;

import org.guidewire.taskmanager.dto.request.SubTaskRequest;
import org.guidewire.taskmanager.dto.response.SubTaskResponse;
import org.guidewire.taskmanager.services.SubTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subtasks")
public class SubTaskController {
    private final SubTaskService subTaskService;

    @Autowired
    public SubTaskController(SubTaskService subTaskService) {
        this.subTaskService = subTaskService;
    }

    @PostMapping
    public ResponseEntity<SubTaskResponse> createSubTask(@RequestBody SubTaskRequest request) {
        return ResponseEntity.ok(subTaskService.createSubTask(request));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<SubTaskResponse>> getSubTasksByTaskId(@PathVariable UUID taskId) {
        return ResponseEntity.ok(subTaskService.getSubTasksByTaskId(taskId));
    }

    @DeleteMapping("/{subTaskId}")
    public ResponseEntity<Void> deleteSubTask(@PathVariable UUID subTaskId) {
        subTaskService.deleteSubTask(subTaskId);
        return ResponseEntity.noContent().build();
    }
}
