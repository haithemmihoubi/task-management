package com.haithem.taskmanagemnt.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haithem.taskmanagemnt.dto.TaskRequest;
import com.haithem.taskmanagemnt.dto.TaskResponse;
import com.haithem.taskmanagemnt.model.TaskStatus;
import com.haithem.taskmanagemnt.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full integration tests for the Task Management API
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("Task Management API Integration Tests")
class TaskManagementIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:7.0"))
            .withExposedPorts(27017);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> mongoDBContainer.getConnectionString());
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    @DisplayName("Integration Test: Complete CRUD workflow")
    void testCompleteCRUDWorkflow() throws Exception {
        // 1. Create a task
        TaskRequest createRequest = TaskRequest.builder()
                .title("Integration Test Task")
                .description("Testing complete workflow")
                .status(TaskStatus.TODO)
                .priority(3)
                .dueDate(LocalDate.now().plusDays(7))
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Integration Test Task"))
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        TaskResponse createdTask = objectMapper.readValue(responseBody, TaskResponse.class);
        String taskId = createdTask.getId();

        // 2. Get the created task by ID
        mockMvc.perform(get("/api/v1/tasks/" + taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.title").value("Integration Test Task"));

        // 3. Update the task
        TaskRequest updateRequest = TaskRequest.builder()
                .title("Updated Integration Task")
                .description("Updated description")
                .status(TaskStatus.IN_PROGRESS)
                .priority(5)
                .dueDate(LocalDate.now().plusDays(3))
                .build();

        mockMvc.perform(put("/api/v1/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Integration Task"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.priority").value(5));

        // 4. Get all tasks
        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // 5. Delete the task
        mockMvc.perform(delete("/api/v1/tasks/" + taskId))
                .andExpect(status().isNoContent());

        // 6. Verify task is deleted
        mockMvc.perform(get("/api/v1/tasks/" + taskId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Integration Test: Multiple tasks with filtering")
    void testMultipleTasksWithFiltering() throws Exception {
        // Create multiple tasks
        TaskRequest task1 = TaskRequest.builder()
                .title("High Priority Task")
                .description("Urgent task")
                .status(TaskStatus.TODO)
                .priority(5)
                .dueDate(LocalDate.now().plusDays(1))
                .build();

        TaskRequest task2 = TaskRequest.builder()
                .title("Low Priority Task")
                .description("Can wait")
                .status(TaskStatus.TODO)
                .priority(1)
                .dueDate(LocalDate.now().plusDays(10))
                .build();

        TaskRequest task3 = TaskRequest.builder()
                .title("In Progress Task")
                .description("Working on it")
                .status(TaskStatus.IN_PROGRESS)
                .priority(3)
                .dueDate(LocalDate.now().plusDays(5))
                .build();

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task2)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task3)))
                .andExpect(status().isCreated());

        // Test getting all tasks
        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

        // Test filtering by status
        mockMvc.perform(get("/api/v1/tasks")
                .param("status", "TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // Test filtering by priority
        mockMvc.perform(get("/api/v1/tasks")
                .param("priority", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("High Priority Task"));

        // Test search functionality
        mockMvc.perform(get("/api/v1/tasks")
                .param("search", "Priority"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("Integration Test: Validation errors")
    void testValidationErrors() throws Exception {
        // Test with blank title
        TaskRequest invalidRequest1 = TaskRequest.builder()
                .title("")
                .description("Description")
                .status(TaskStatus.TODO)
                .priority(3)
                .build();

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest1)))
                .andExpect(status().isBadRequest());

        // Test with null status
        TaskRequest invalidRequest2 = TaskRequest.builder()
                .title("Valid Title")
                .description("Description")
                .status(null)
                .priority(3)
                .build();

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest2)))
                .andExpect(status().isBadRequest());

        // Test with invalid priority
        TaskRequest invalidRequest3 = TaskRequest.builder()
                .title("Valid Title")
                .description("Description")
                .status(TaskStatus.TODO)
                .priority(10)
                .build();

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest3)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Integration Test: Task sorting")
    void testTaskSorting() throws Exception {
        // Create tasks with different due dates
        for (int i = 1; i <= 3; i++) {
            TaskRequest task = TaskRequest.builder()
                    .title("Task " + i)
                    .description("Description " + i)
                    .status(TaskStatus.TODO)
                    .priority(i)
                    .dueDate(LocalDate.now().plusDays(i))
                    .build();

            mockMvc.perform(post("/api/v1/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(task)))
                    .andExpect(status().isCreated());
        }

        // Test ascending sort by priority
        mockMvc.perform(get("/api/v1/tasks")
                .param("sortBy", "priority")
                .param("sortDirection", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].priority").value(1))
                .andExpect(jsonPath("$[2].priority").value(3));

        // Test descending sort by priority
        mockMvc.perform(get("/api/v1/tasks")
                .param("sortBy", "priority")
                .param("sortDirection", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].priority").value(3))
                .andExpect(jsonPath("$[2].priority").value(1));
    }

    @Test
    @DisplayName("Integration Test: Date range filtering")
    void testDateRangeFiltering() throws Exception {
        LocalDate now = LocalDate.now();

        // Create tasks with different due dates
        TaskRequest task1 = TaskRequest.builder()
                .title("Near Task")
                .description("Due soon")
                .status(TaskStatus.TODO)
                .priority(3)
                .dueDate(now.plusDays(2))
                .build();

        TaskRequest task2 = TaskRequest.builder()
                .title("Far Task")
                .description("Due later")
                .status(TaskStatus.TODO)
                .priority(3)
                .dueDate(now.plusDays(20))
                .build();

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task2)))
                .andExpect(status().isCreated());

        // Filter tasks within next 7 days
        mockMvc.perform(get("/api/v1/tasks")
                .param("dueDateFrom", now.toString())
                .param("dueDateTo", now.plusDays(7).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Near Task"));
    }

    @Test
    @DisplayName("Integration Test: Resource not found handling")
    void testResourceNotFoundHandling() throws Exception {
        // Try to get non-existent task
        mockMvc.perform(get("/api/v1/tasks/non-existent-id"))
                .andExpect(status().isNotFound());

        // Try to update non-existent task
        TaskRequest updateRequest = TaskRequest.builder()
                .title("Update")
                .description("Description")
                .status(TaskStatus.TODO)
                .priority(3)
                .build();

        mockMvc.perform(put("/api/v1/tasks/non-existent-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());

        // Try to delete non-existent task
        mockMvc.perform(delete("/api/v1/tasks/non-existent-id"))
                .andExpect(status().isNotFound());
    }
}
