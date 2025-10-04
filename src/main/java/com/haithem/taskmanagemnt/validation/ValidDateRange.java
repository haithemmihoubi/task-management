package com.haithem.taskmanagemnt.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation to validate date range
 */
@Documented
@Constraint(validatedBy = DateRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {
    String message() default "dueDateFrom must be before or equal to dueDateTo";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String from();
    String to();
}

