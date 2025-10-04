package com.haithem.taskmanagemnt.service;

import com.haithem.taskmanagemnt.dto.TaskFilterRequest;
import com.haithem.taskmanagemnt.dto.TaskRequest;
import com.haithem.taskmanagemnt.dto.TaskResponse;
import com.haithem.taskmanagemnt.exception.ResourceNotFoundException;
import com.haithem.taskmanagemnt.model.Task;
import com.haithem.taskmanagemnt.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Task operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final MongoTemplate mongoTemplate;

    /**
     * Create a new task
     */
    public TaskResponse createTask(TaskRequest request) {
        log.info("Creating new task with title: {}", request.getTitle());

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .build();

        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with id: {}", savedTask.getId());

        return mapToResponse(savedTask);
    }

    /**
     * Get all tasks with advanced filtering
     */
    public List<TaskResponse> getTasks(TaskFilterRequest filter) {
        log.info("Fetching tasks with filter: {}", filter);

        Query query = new Query();

        // Apply filters
        if (filter.getStatus() != null) {
            query.addCriteria(Criteria.where("status").is(filter.getStatus()));
        }

        if (filter.getPriority() != null) {
            query.addCriteria(Criteria.where("priority").is(filter.getPriority()));
        }

        if (filter.getDueDateFrom() != null && filter.getDueDateTo() != null) {
            query.addCriteria(Criteria.where("dueDate")
                    .gte(filter.getDueDateFrom())
                    .lte(filter.getDueDateTo()));
        } else if (filter.getDueDateFrom() != null) {
            query.addCriteria(Criteria.where("dueDate").gte(filter.getDueDateFrom()));
        } else if (filter.getDueDateTo() != null) {
            query.addCriteria(Criteria.where("dueDate").lte(filter.getDueDateTo()));
        }

        // Case-insensitive search in title and description
        if (filter.getSearch() != null && !filter.getSearch().trim().isEmpty()) {
            String searchPattern = ".*" + filter.getSearch() + ".*";
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("title").regex(searchPattern, "i"),
                    Criteria.where("description").regex(searchPattern, "i")
            );
            query.addCriteria(searchCriteria);
        }

        // Apply sorting
        if (filter.getSortBy() != null && !filter.getSortBy().trim().isEmpty()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSortDirection())
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            query.with(Sort.by(direction, filter.getSortBy()));
        }

        List<Task> tasks = mongoTemplate.find(query, Task.class);
        log.info("Found {} tasks matching the criteria", tasks.size());

        return tasks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get task by ID
     */
    public TaskResponse getTaskById(String id) {
        log.info("Fetching task with id: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        return mapToResponse(task);
    }

    /**
     * Update an existing task
     */
    public TaskResponse updateTask(String id, TaskRequest request) {
        log.info("Updating task with id: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        Task updatedTask = taskRepository.save(task);
        log.info("Task updated successfully with id: {}", updatedTask.getId());

        return mapToResponse(updatedTask);
    }

    /**
     * Delete a task
     */
    public void deleteTask(String id) {
        log.info("Deleting task with id: {}", id);

        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }

        taskRepository.deleteById(id);
        log.info("Task deleted successfully with id: {}", id);
    }

    /**
     * Map Task entity to TaskResponse DTO
     */
    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .build();
    }
}

