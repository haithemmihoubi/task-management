package com.haithem.taskmanagemnt;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/test-taskdb",})
class TaskManagemntApplicationTests {

    @Test
    void contextLoads() {
        // This test verifies that the application context loads successfully


    }

}
