package com.haithem.taskmanagemnt.repository;

import com.haithem.taskmanagemnt.model.Task;
import com.haithem.taskmanagemnt.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for TaskRepository using Testcontainers
 */
@DataMongoTest
@Testcontainers
@DisplayName("TaskRepository Integration Tests")
class TaskRepositoryTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:7.0"))
            .withExposedPorts(27017);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> mongoDBContainer.getConnectionString());
    }

    @Autowired
    private TaskRepository taskRepository;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();

        sampleTask = Task.builder()
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .priority(3)
                .dueDate(LocalDate.now().plusDays(7))
                .build();
    }

    @Test
    @DisplayName("Should save task successfully")
    void testSaveTask() {
        // When
        Task savedTask = taskRepository.save(sampleTask);

        // Then
        assertThat(savedTask).isNotNull();
        assertThat(savedTask.getId()).isNotNull();
        assertThat(savedTask.getTitle()).isEqualTo("Test Task");
        assertThat(savedTask.getStatus()).isEqualTo(TaskStatus.TODO);
    }

    @Test
    @DisplayName("Should find task by ID")
    void testFindById() {
        // Given
        Task savedTask = taskRepository.save(sampleTask);

        // When
        Optional<Task> foundTask = taskRepository.findById(savedTask.getId());

        // Then
        assertThat(foundTask).isPresent();
        assertThat(foundTask.get().getTitle()).isEqualTo("Test Task");
        assertThat(foundTask.get().getId()).isEqualTo(savedTask.getId());
    }

    @Test
    @DisplayName("Should return empty when task not found by ID")
    void testFindById_NotFound() {
        // When
        Optional<Task> foundTask = taskRepository.findById("non-existent-id");

        // Then
        assertThat(foundTask).isEmpty();
    }

    @Test
    @DisplayName("Should find all tasks")
    void testFindAll() {
        // Given
        Task task2 = Task.builder()
                .title("Another Task")
                .description("Another Description")
                .status(TaskStatus.IN_PROGRESS)
                .priority(5)
                .dueDate(LocalDate.now().plusDays(3))
                .build();

        taskRepository.save(sampleTask);
        taskRepository.save(task2);

        // When
        List<Task> tasks = taskRepository.findAll();

        // Then
        assertThat(tasks).hasSize(2);
        assertThat(tasks).extracting(Task::getTitle)
                .containsExactlyInAnyOrder("Test Task", "Another Task");
    }

    @Test
    @DisplayName("Should update task successfully")
    void testUpdateTask() {
        // Given
        Task savedTask = taskRepository.save(sampleTask);
        savedTask.setTitle("Updated Title");
        savedTask.setStatus(TaskStatus.DONE);

        // When
        Task updatedTask = taskRepository.save(savedTask);

        // Then
        assertThat(updatedTask.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedTask.getStatus()).isEqualTo(TaskStatus.DONE);
        assertThat(updatedTask.getId()).isEqualTo(savedTask.getId());
    }

    @Test
    @DisplayName("Should delete task successfully")
    void testDeleteTask() {
        // Given
        Task savedTask = taskRepository.save(sampleTask);

        // When
        taskRepository.deleteById(savedTask.getId());

        // Then
        Optional<Task> deletedTask = taskRepository.findById(savedTask.getId());
        assertThat(deletedTask).isEmpty();
    }

    @Test
    @DisplayName("Should check if task exists by ID")
    void testExistsById() {
        // Given
        Task savedTask = taskRepository.save(sampleTask);

        // When
        boolean exists = taskRepository.existsById(savedTask.getId());
        boolean notExists = taskRepository.existsById("non-existent-id");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should count all tasks")
    void testCount() {
        // Given
        taskRepository.save(sampleTask);
        Task task2 = Task.builder()
                .title("Task 2")
                .description("Description 2")
                .status(TaskStatus.IN_PROGRESS)
                .priority(4)
                .dueDate(LocalDate.now().plusDays(5))
                .build();
        taskRepository.save(task2);

        // When
        long count = taskRepository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should delete all tasks")
    void testDeleteAll() {
        // Given
        taskRepository.save(sampleTask);
        Task task2 = Task.builder()
                .title("Task 2")
                .description("Description 2")
                .status(TaskStatus.IN_PROGRESS)
                .priority(4)
                .dueDate(LocalDate.now().plusDays(5))
                .build();
        taskRepository.save(task2);

        // When
        taskRepository.deleteAll();

        // Then
        List<Task> tasks = taskRepository.findAll();
        assertThat(tasks).isEmpty();
    }
}
