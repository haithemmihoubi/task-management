package com.haithem.taskmanagemnt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import java.util.Optional;

/**
 * Configuration for MongoDB auditing
 * Provides the current auditor for @CreatedBy and @LastModifiedBy fields
 */
@Configuration
@EnableMongoAuditing
public class AuditorAwareConfig {

    /**
     * Returns the current auditor (user making the change)
     * In a real application, this would return the authenticated user from SecurityContext
     * For now, returns "system" as placeholder
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            // TODO: Integrate with Spring Security to get authenticated user
            // SecurityContext.getContext().getAuthentication().getName()
            return Optional.of("system");
        };
    }
}

