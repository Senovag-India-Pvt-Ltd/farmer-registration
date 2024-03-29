package com.sericulture.registration.service;

import com.sericulture.registration.model.entity.BaseEntity;
import com.sericulture.registration.model.exceptions.ValidationException;
import com.sericulture.registration.model.exceptions.Message;
import com.sericulture.registration.model.exceptions.ValidationMessage;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CustomValidator {

    @Autowired
    Validator validator;

    public <T extends BaseEntity> void validate(T entity) {
        Set<ConstraintViolation<T>> violations = validator.validate(entity);
        violations.stream()
                        .map(cv -> new ValidationMessage(cv.getPropertyPath().toString(), cv.getMessage()))
                        .collect(Collectors.collectingAndThen(Collectors.toList(),
                        result -> {
                            if (!result.isEmpty()) throw new ValidationException(result);
                            return null;
                        }));
    }
}