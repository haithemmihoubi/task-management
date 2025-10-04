package com.haithem.taskmanagemnt.config;

import com.haithem.taskmanagemnt.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * MongoDB configuration to ensure collections are created on startup
 */
@Configuration
public class MongoConfig {

    private static final Logger logger = LoggerFactory.getLogger(MongoConfig.class);

    /**
     * Creates MongoDB collections on application startup if they don't exist
     */
    @Bean
    CommandLineRunner initDatabase(MongoTemplate mongoTemplate) {
        return args -> {
            try {
                // Create tasks collection if it doesn't exist
                if (!mongoTemplate.collectionExists(Task.class)) {
                    mongoTemplate.createCollection(Task.class);
                    logger.info("✓ Created 'tasks' collection in MongoDB");
                } else {
                    logger.info("✓ 'tasks' collection already exists in MongoDB");
                }
            } catch (Exception e) {
                logger.error("✗ Failed to create/check 'tasks' collection. Error: {}", e.getMessage());
                logger.error("Please check your MongoDB connection settings and credentials.");
                throw e;
            }
        };
    }
}
