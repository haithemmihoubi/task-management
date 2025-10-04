package com.haithem.taskmanagemnt.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for API documentation
 * Access at: http://localhost:8080/swagger-ui.html
 * OpenAPI JSON: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${spring.application.name:task-management-api}")
    private String applicationName;

    @Bean
    public OpenAPI taskManagementOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:" + serverPort);
        localServer.setDescription("Local Development Server");

        Contact contact = new Contact();
        contact.setEmail("haithemmihoubi1234@gmail.com");
        contact.setName("Haithem Mihoubi");
        contact.setUrl("https://github.com/haithemmihoubi");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("Task Management API")
                .version("1.0.0")
                .contact(contact)
                .description("""
                        A production-ready RESTful API for managing tasks with advanced features:
                        
                        **Features:**
                        -  CRUD operations for tasks
                        -  Advanced filtering and sorting
                        -  Input validation
                        -  Rate limiting (100 requests/minute per IP)
                        -  Audit fields (created/updated by/at)
                        - ✅ MongoDB integration
                        - ✅ Comprehensive error handling
                        - ✅ MapStruct DTO mapping
                        
                        Built with Spring Boot 3.5.6, MongoDB, and best practices.
                        """)
                .license(license);

        Tag taskTag = new Tag()
                .name("Task Management")
                .description("Operations for managing tasks");

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer))
                .tags(List.of(taskTag));
    }
}
