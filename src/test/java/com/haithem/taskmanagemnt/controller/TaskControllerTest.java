package com.haithem.taskmanagemnt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haithem.taskmanagemnt.dto.TaskRequest;
import com.haithem.taskmanagemnt.dto.TaskResponse;
import com.haithem.taskmanagemnt.exception.ResourceNotFoundException;
import com.haithem.taskmanagemnt.model.TaskStatus;
import com.haithem.taskmanagemnt.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for TaskController
 */
@WebMvcTest(TaskController.class)
@DisplayName("TaskController Integration Tests")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    private TaskRequest taskRequest;
    private TaskResponse taskResponse;

    @BeforeEach
    void setUp() {
        taskRequest = TaskRequest.builder()
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .priority(3)
                .dueDate(LocalDate.now().plusDays(7))
                .build();

        taskResponse = TaskResponse.builder()
                .id("1")
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .priority(3)
                .dueDate(LocalDate.now().plusDays(7))
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/tasks - Should create task successfully")
    void testCreateTask_Success() throws Exception {
        // Given
        when(taskService.createTask(any(TaskRequest.class))).thenReturn(taskResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.priority").value(3));

        verify(taskService, times(1)).createTask(any(TaskRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/tasks - Should return 400 when title is blank")
    void testCreateTask_BlankTitle() throws Exception {
        // Given
        taskRequest.setTitle("");

        // When & Then
        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any(TaskRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/tasks - Should return 400 when status is null")
    void testCreateTask_NullStatus() throws Exception {
        // Given
        taskRequest.setStatus(null);

        // When & Then
        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any(TaskRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/tasks - Should return 400 when priority is out of range")
    void testCreateTask_InvalidPriority() throws Exception {
        // Given
        taskRequest.setPriority(10); // Invalid: should be 1-5

        // When & Then
        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any(TaskRequest.class));
    }

    @Test
    @DisplayName("GET /api/v1/tasks - Should return all tasks")
    void testGetTasks_Success() throws Exception {
        // Given
        TaskResponse task2 = TaskResponse.builder()
                .id("2")
                .title("Another Task")
                .description("Another Description")
                .status(TaskStatus.IN_PROGRESS)
                .priority(5)
                .dueDate(LocalDate.now().plusDays(3))
                .build();

        List<TaskResponse> tasks = Arrays.asList(taskResponse, task2);
        when(taskService.getTasks(any())).thenReturn(tasks);

        // When & Then
        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].title").value("Test Task"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].title").value("Another Task"));

        verify(taskService, times(1)).getTasks(any());
    }

    @Test
    @DisplayName("GET /api/v1/tasks?status=TODO - Should return filtered tasks")
    void testGetTasks_WithStatusFilter() throws Exception {
        // Given
        when(taskService.getTasks(any())).thenReturn(Arrays.asList(taskResponse));

        // When & Then
        mockMvc.perform(get("/api/v1/tasks")
                        .param("status", "TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status").value("TODO"));

        verify(taskService, times(1)).getTasks(any());
    }

    @Test
    @DisplayName("GET /api/v1/tasks?priority=3 - Should return tasks with priority filter")
    void testGetTasks_WithPriorityFilter() throws Exception {
        // Given
        when(taskService.getTasks(any())).thenReturn(Arrays.asList(taskResponse));

        // When & Then
        mockMvc.perform(get("/api/v1/tasks")
                        .param("priority", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].priority").value(3));

        verify(taskService, times(1)).getTasks(any());
    }

    @Test
    @DisplayName("GET /api/v1/tasks?search=Test - Should return tasks matching search")
    void testGetTasks_WithSearchFilter() throws Exception {
        // Given
        when(taskService.getTasks(any())).thenReturn(Arrays.asList(taskResponse));

        // When & Then
        mockMvc.perform(get("/api/v1/tasks")
                        .param("search", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value(containsString("Test")));

        verify(taskService, times(1)).getTasks(any());
    }

    @Test
    @DisplayName("GET /api/v1/tasks/{id} - Should return task by ID")
    void testGetTaskById_Success() throws Exception {
        // Given
        when(taskService.getTaskById("1")).thenReturn(taskResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"));

        verify(taskService, times(1)).getTaskById("1");
    }

    @Test
    @DisplayName("GET /api/v1/tasks/{id} - Should return 404 when task not found")
    void testGetTaskById_NotFound() throws Exception {
        // Given
        when(taskService.getTaskById("999"))
                .thenThrow(new ResourceNotFoundException("Task not found with id: 999"));

        // When & Then
        mockMvc.perform(get("/api/v1/tasks/999"))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).getTaskById("999");
    }

    @Test
    @DisplayName("PUT /api/v1/tasks/{id} - Should update task successfully")
    void testUpdateTask_Success() throws Exception {
        // Given
        TaskRequest updateRequest = TaskRequest.builder()
                .title("Updated Task")
                .description("Updated Description")
                .status(TaskStatus.DONE)
                .priority(5)
                .dueDate(LocalDate.now().plusDays(1))
                .build();

        TaskResponse updatedResponse = TaskResponse.builder()
                .id("1")
                .title("Updated Task")
                .description("Updated Description")
                .status(TaskStatus.DONE)
                .priority(5)
                .dueDate(LocalDate.now().plusDays(1))
                .build();

        when(taskService.updateTask(eq("1"), any(TaskRequest.class))).thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.status").value("DONE"))
                .andExpect(jsonPath("$.priority").value(5));

        verify(taskService, times(1)).updateTask(eq("1"), any(TaskRequest.class));
    }

    @Test
    @DisplayName("PUT /api/v1/tasks/{id} - Should return 404 when updating non-existent task")
    void testUpdateTask_NotFound() throws Exception {
        // Given
        when(taskService.updateTask(eq("999"), any(TaskRequest.class)))
                .thenThrow(new ResourceNotFoundException("Task not found with id: 999"));

        // When & Then
        mockMvc.perform(put("/api/v1/tasks/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).updateTask(eq("999"), any(TaskRequest.class));
    }

    @Test
    @DisplayName("PUT /api/v1/tasks/{id} - Should return 400 when update request is invalid")
    void testUpdateTask_InvalidRequest() throws Exception {
        // Given
        taskRequest.setTitle(""); // Invalid

        // When & Then
        mockMvc.perform(put("/api/v1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).updateTask(anyString(), any(TaskRequest.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/tasks/{id} - Should delete task successfully")
    void testDeleteTask_Success() throws Exception {
        // Given
        doNothing().when(taskService).deleteTask("1");

        // When & Then
        mockMvc.perform(delete("/api/v1/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask("1");
    }

    @Test
    @DisplayName("DELETE /api/v1/tasks/{id} - Should return 404 when deleting non-existent task")
    void testDeleteTask_NotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Task not found with id: 999"))
                .when(taskService).deleteTask("999");

        // When & Then
        mockMvc.perform(delete("/api/v1/tasks/999"))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).deleteTask("999");
    }

    @Test
    @DisplayName("GET /api/v1/tasks - Should return empty list when no tasks exist")
    void testGetTasks_EmptyList() throws Exception {
        // Given
        when(taskService.getTasks(any())).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(taskService, times(1)).getTasks(any());
    }
}
