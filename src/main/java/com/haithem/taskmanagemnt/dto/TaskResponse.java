package com.haithem.taskmanagemnt.dto;

import com.haithem.taskmanagemnt.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for task responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private String id;
    private String title;
    private String description;
    private TaskStatus status;
    private Integer priority;
    private LocalDate dueDate;
}

