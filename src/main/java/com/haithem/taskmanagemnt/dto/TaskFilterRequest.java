package com.haithem.taskmanagemnt.dto;

import com.haithem.taskmanagemnt.model.TaskStatus;
import com.haithem.taskmanagemnt.validation.ValidDateRange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for filtering tasks with validation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidDateRange(from = "dueDateFrom", to = "dueDateTo")
public class TaskFilterRequest {
    private TaskStatus status;
    private Integer priority;
    private LocalDate dueDateFrom;
    private LocalDate dueDateTo;
    private String search;
    private String sortBy;
    private String sortDirection;
}
