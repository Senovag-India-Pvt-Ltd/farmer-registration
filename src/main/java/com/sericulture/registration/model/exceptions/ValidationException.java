package com.sericulture.registration.model.exceptions;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class ValidationException extends RuntimeException{

    private List<String> errorMessages = new ArrayList<>();

    public ValidationException(String errorMessage) {
        errorMessages.add(errorMessage);
    }

    public ValidationException(List<String> errorMessage) {
        errorMessages.addAll(errorMessage);
    }
    public ValidationException(Set<String> errorMessage) {
        errorMessages.addAll(errorMessage);
    }
    @Override
    public String getMessage() {

        String msg = errorMessages.stream()
                .collect(Collectors.joining(", ", "[", "]"));
        return msg;
    }
}
