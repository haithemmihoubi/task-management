package com.haithem.taskmanagemnt.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation for task priority
 * Validates that priority is between 1 and 5
 */
@Documented
@Constraint(validatedBy = PriorityValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPriority {

    String message() default "Priority must be between 1 and 5";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int min() default 1;

    int max() default 5;
}

