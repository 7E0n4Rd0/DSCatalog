package com.leonardo.DSCatalog.resources.exceptions;

import com.leonardo.DSCatalog.services.exceptions.DatabaseException;
import com.leonardo.DSCatalog.services.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> entityNotFound(ResourceNotFoundException e, HttpServletRequest request){
        StandardError err = new StandardError(Instant.now(), HttpStatus.NOT_FOUND.value(),
                "Resource not Found", e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<StandardError> integrityViolation(DatabaseException e, HttpServletRequest request){
        StandardError err = new StandardError(Instant.now(), HttpStatus.BAD_REQUEST.value(),
                "Fail on Referential Integrity", e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> methodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request){
        ValidationError err = new ValidationError(Instant.now(), HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Invalid Data", e.getMessage(), request.getRequestURI());
        e.getBindingResult().getFieldErrors()
                .forEach(x -> err.addError(x.getField(), x.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(err);
    }

}
