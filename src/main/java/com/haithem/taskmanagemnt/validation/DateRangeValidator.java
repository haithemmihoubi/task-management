package com.haithem.taskmanagemnt.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import java.time.LocalDate;

/**
 * Validator for ValidDateRange annotation
 */
public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

    private String fromField;
    private String toField;

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
        this.fromField = constraintAnnotation.from();
        this.toField = constraintAnnotation.to();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);
        LocalDate fromDate = (LocalDate) beanWrapper.getPropertyValue(fromField);
        LocalDate toDate = (LocalDate) beanWrapper.getPropertyValue(toField);

        if (fromDate == null || toDate == null) {
            return true;
        }

        return !fromDate.isAfter(toDate);
    }
}

