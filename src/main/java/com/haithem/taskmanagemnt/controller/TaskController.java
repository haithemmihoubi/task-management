package com.haithem.taskmanagemnt.controller;

import com.haithem.taskmanagemnt.dto.TaskFilterRequest;
import com.haithem.taskmanagemnt.dto.TaskRequest;
import com.haithem.taskmanagemnt.dto.TaskResponse;
import com.haithem.taskmanagemnt.model.TaskStatus;
import com.haithem.taskmanagemnt.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"})
@Tag(name = "Task Management", description = "REST API for managing tasks with CRUD operations, filtering, and sorting")
public class TaskController {

    private final TaskService taskService;

    /**
     * Create a new task
     */
    @PostMapping
    @Operation(
        summary = "Create a new task",
        description = "Creates a new task with the provided details. All fields except description and dueDate are required."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Task created successfully",
            content = @Content(schema = @Schema(implementation = TaskResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "429", description = "Too many requests")
    })
    public ResponseEntity<TaskResponse> createTask(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Task details to create",
                required = true
            )
            @Valid @RequestBody TaskRequest request) {
        log.info("Received request to create task: {}", request);
        TaskResponse response = taskService.createTask(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get all tasks with optional filtering
     */
    @GetMapping
    @Operation(
        summary = "Get all tasks",
        description = "Retrieves all tasks with optional filtering by status, priority, due date range, and search term. Supports sorting."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
        @ApiResponse(responseCode = "429", description = "Too many requests")
    })
    public ResponseEntity<List<TaskResponse>> getTasks(
            @Parameter(description = "Filter by task status (TODO, IN_PROGRESS, COMPLETED)")
            @RequestParam(required = false) TaskStatus status,

            @Parameter(description = "Filter by priority level (1-5)")
            @RequestParam(required = false) Integer priority,

            @Parameter(description = "Filter tasks from this due date (inclusive)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateFrom,

            @Parameter(description = "Filter tasks until this due date (inclusive)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateTo,

            @Parameter(description = "Search term for title and description")
            @RequestParam(required = false) String search,

            @Parameter(description = "Sort field (e.g., dueDate, priority, createdAt)")
            @RequestParam(required = false, defaultValue = "dueDate") String sortBy,

            @Parameter(description = "Sort direction (asc or desc)")
            @RequestParam(required = false, defaultValue = "asc") String sortDirection
    ) {
        log.info("Received request to get tasks with filters");

        TaskFilterRequest filter = TaskFilterRequest.builder()
                .status(status)
                .priority(priority)
                .dueDateFrom(dueDateFrom)
                .dueDateTo(dueDateTo)
                .search(search)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        List<TaskResponse> tasks = taskService.getTasks(filter);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get task by ID
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get task by ID",
        description = "Retrieves a specific task by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task found",
            content = @Content(schema = @Schema(implementation = TaskResponse.class))),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "429", description = "Too many requests")
    })
    public ResponseEntity<TaskResponse> getTaskById(
            @Parameter(description = "Task ID", required = true)
            @PathVariable String id) {
        log.info("Received request to get task with id: {}", id);
        TaskResponse response = taskService.getTaskById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Update an existing task
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Update a task",
        description = "Updates an existing task with new details. All fields in the request will update the task."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task updated successfully",
            content = @Content(schema = @Schema(implementation = TaskResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "429", description = "Too many requests")
    })
    public ResponseEntity<TaskResponse> updateTask(
            @Parameter(description = "Task ID", required = true)
            @PathVariable String id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Updated task details",
                required = true
            )
            @Valid @RequestBody TaskRequest request
    ) {
        log.info("Received request to update task with id: {}", id);
        TaskResponse response = taskService.updateTask(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a task
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a task",
        description = "Permanently deletes a task by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "429", description = "Too many requests")
    })
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "Task ID", required = true)
            @PathVariable String id) {
        log.info("Received request to delete task with id: {}", id);
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
