package com.haithem.taskmanagemnt.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation for task status enum
 */
@Documented
@Constraint(validatedBy = TaskStatusValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTaskStatus {

    String message() default "Invalid task status. Must be one of: TODO, IN_PROGRESS, COMPLETED";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

