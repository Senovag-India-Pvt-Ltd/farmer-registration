package com.sericulture.registration.handlers;

import com.sericulture.registration.model.api.ErrorResponse;
import com.sericulture.registration.model.api.ErrorType;
import com.sericulture.registration.model.exceptions.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
public class GlobalAPIExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleEmployeeNotFound(
            ValidationException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(exception.getErrorMessages(), ErrorType.VALIDATION));
    }
}