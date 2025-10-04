package com.haithem.taskmanagemnt.dto;

import com.haithem.taskmanagemnt.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for filtering tasks
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskFilterRequest {

    private TaskStatus status;
    private Integer priority;
    private LocalDate dueDateFrom;
    private LocalDate dueDateTo;
    private String search; // For title/description search
    private String sortBy; // dueDate or priority
    private String sortDirection; // asc or desc
}

