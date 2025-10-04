package com.haithem.taskmanagemnt.service;

import com.haithem.taskmanagemnt.dto.TaskFilterRequest;
import com.haithem.taskmanagemnt.dto.TaskRequest;
import com.haithem.taskmanagemnt.dto.TaskResponse;
import com.haithem.taskmanagemnt.exception.ResourceNotFoundException;
import com.haithem.taskmanagemnt.model.Task;
import com.haithem.taskmanagemnt.model.TaskStatus;
import com.haithem.taskmanagemnt.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TaskService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService Unit Tests")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;
    private TaskRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleTask = Task.builder()
                .id("1")
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .priority(3)
                .dueDate(LocalDate.now().plusDays(7))
                .build();

        sampleRequest = TaskRequest.builder()
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .priority(3)
                .dueDate(LocalDate.now().plusDays(7))
                .build();
    }

    @Test
    @DisplayName("Should create task successfully")
    void testCreateTask_Success() {
        // Given
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        // When
        TaskResponse response = taskService.createTask(sampleRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("1");
        assertThat(response.getTitle()).isEqualTo("Test Task");
        assertThat(response.getStatus()).isEqualTo(TaskStatus.TODO);
        assertThat(response.getPriority()).isEqualTo(3);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should get task by ID successfully")
    void testGetTaskById_Success() {
        // Given
        when(taskRepository.findById("1")).thenReturn(Optional.of(sampleTask));

        // When
        TaskResponse response = taskService.getTaskById("1");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("1");
        assertThat(response.getTitle()).isEqualTo("Test Task");
        verify(taskRepository, times(1)).findById("1");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when task not found")
    void testGetTaskById_NotFound() {
        // Given
        when(taskRepository.findById("999")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.getTaskById("999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found with id: 999");
        verify(taskRepository, times(1)).findById("999");
    }

    @Test
    @DisplayName("Should get all tasks with filter")
    void testGetTasks_WithFilter() {
        // Given
        Task task2 = Task.builder()
                .id("2")
                .title("Another Task")
                .description("Another Description")
                .status(TaskStatus.IN_PROGRESS)
                .priority(5)
                .dueDate(LocalDate.now().plusDays(3))
                .build();

        List<Task> tasks = Arrays.asList(sampleTask, task2);
        when(mongoTemplate.find(any(Query.class), eq(Task.class))).thenReturn(tasks);

        TaskFilterRequest filter = TaskFilterRequest.builder()
                .status(TaskStatus.TODO)
                .priority(3)
                .build();

        // When
        List<TaskResponse> responses = taskService.getTasks(filter);

        // Then
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(2);
        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Task.class));
    }

    @Test
    @DisplayName("Should update task successfully")
    void testUpdateTask_Success() {
        // Given
        TaskRequest updateRequest = TaskRequest.builder()
                .title("Updated Task")
                .description("Updated Description")
                .status(TaskStatus.DONE)
                .priority(5)
                .dueDate(LocalDate.now().plusDays(1))
                .build();

        Task updatedTask = Task.builder()
                .id("1")
                .title("Updated Task")
                .description("Updated Description")
                .status(TaskStatus.DONE)
                .priority(5)
                .dueDate(LocalDate.now().plusDays(1))
                .build();

        when(taskRepository.findById("1")).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        // When
        TaskResponse response = taskService.updateTask("1", updateRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Updated Task");
        assertThat(response.getStatus()).isEqualTo(TaskStatus.DONE);
        assertThat(response.getPriority()).isEqualTo(5);
        verify(taskRepository, times(1)).findById("1");
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent task")
    void testUpdateTask_NotFound() {
        // Given
        when(taskRepository.findById("999")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.updateTask("999", sampleRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found with id: 999");
        verify(taskRepository, times(1)).findById("999");
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Should delete task successfully")
    void testDeleteTask_Success() {
        // Given
        when(taskRepository.existsById("1")).thenReturn(true);
        doNothing().when(taskRepository).deleteById("1");

        // When
        taskService.deleteTask("1");

        // Then
        verify(taskRepository, times(1)).existsById("1");
        verify(taskRepository, times(1)).deleteById("1");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent task")
    void testDeleteTask_NotFound() {
        // Given
        when(taskRepository.existsById("999")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> taskService.deleteTask("999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found with id: 999");
        verify(taskRepository, times(1)).existsById("999");
        verify(taskRepository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("Should get tasks with search filter")
    void testGetTasks_WithSearchFilter() {
        // Given
        List<Task> tasks = Arrays.asList(sampleTask);
        when(mongoTemplate.find(any(Query.class), eq(Task.class))).thenReturn(tasks);

        TaskFilterRequest filter = TaskFilterRequest.builder()
                .search("Test")
                .build();

        // When
        List<TaskResponse> responses = taskService.getTasks(filter);

        // Then
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getTitle()).contains("Test");
        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Task.class));
    }

    @Test
    @DisplayName("Should get tasks with date range filter")
    void testGetTasks_WithDateRangeFilter() {
        // Given
        List<Task> tasks = Arrays.asList(sampleTask);
        when(mongoTemplate.find(any(Query.class), eq(Task.class))).thenReturn(tasks);

        TaskFilterRequest filter = TaskFilterRequest.builder()
                .dueDateFrom(LocalDate.now())
                .dueDateTo(LocalDate.now().plusDays(10))
                .build();

        // When
        List<TaskResponse> responses = taskService.getTasks(filter);

        // Then
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(1);
        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Task.class));
    }

    @Test
    @DisplayName("Should return empty list when no tasks match filter")
    void testGetTasks_EmptyResult() {
        // Given
        when(mongoTemplate.find(any(Query.class), eq(Task.class))).thenReturn(Arrays.asList());

        TaskFilterRequest filter = TaskFilterRequest.builder()
                .status(TaskStatus.DONE)
                .build();

        // When
        List<TaskResponse> responses = taskService.getTasks(filter);

        // Then
        assertThat(responses).isNotNull();
        assertThat(responses).isEmpty();
        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Task.class));
    }
}

