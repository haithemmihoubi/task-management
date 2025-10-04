package com.haithem.taskmanagemnt.validation;

import com.haithem.taskmanagemnt.model.TaskStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for ValidTaskStatus annotation
 */
public class TaskStatusValidator implements ConstraintValidator<ValidTaskStatus, TaskStatus> {

    @Override
    public boolean isValid(TaskStatus value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value == TaskStatus.TODO ||
               value == TaskStatus.IN_PROGRESS ||
               value == TaskStatus.DONE;
    }
}

