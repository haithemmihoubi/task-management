package com.haithem.taskmanagemnt.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for ValidPriority annotation
 */
public class PriorityValidator implements ConstraintValidator<ValidPriority, Integer> {

    private int min;
    private int max;

    @Override
    public void initialize(ValidPriority constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value >= min && value <= max;
    }
}

