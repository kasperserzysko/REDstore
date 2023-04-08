package com.kasperserzysko.tools;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class Validator {

    private final jakarta.validation.Validator validator;

    public <T> void validate(T dto) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
