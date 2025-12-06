package com.realestate.realestate.exeception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // ------------------------------
    // VALIDATION ERRORS (@Valid)
    // ------------------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(err ->
                errors.put(err.getField(), err.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(Map.of(
                "status", 400,
                "error", "Validation Failed",
                "details", errors
        ));
    }

    // ------------------------------
    // ENTITY NOT FOUND
    // ------------------------------
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of(
                "status", 404,
                "error", "Not Found",
                "message", ex.getMessage()
        ));
    }

    // ------------------------------
    // ILLEGAL ARGUMENTS (wrong input)
    // ------------------------------
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "status", 400,
                "error", "Invalid Request",
                "message", ex.getMessage()
        ));
    }

    // ------------------------------
    // DATABASE CONSTRAINT ERRORS
    // ------------------------------
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleSQLConstraint(DataIntegrityViolationException ex) {
        return ResponseEntity.status(409).body(Map.of(
                "status", 409,
                "error", "Data Integrity Violation",
                "message", "Duplicate entry or constraint violation"
        ));
    }

    // ------------------------------
    // VALIDATION: ConstraintViolationException
    // (For @Valid on path variables, etc.)
    // ------------------------------
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(v ->
                errors.put(v.getPropertyPath().toString(), v.getMessage())
        );

        return ResponseEntity.badRequest().body(Map.of(
                "status", 400,
                "error", "Validation Failed",
                "details", errors
        ));
    }

    // ------------------------------
    // GLOBAL CATCH-ALL
    // ------------------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(Exception ex) {

        ex.printStackTrace(); // log error

        return ResponseEntity.status(500).body(Map.of(
                "status", 500,
                "error", "Internal Server Error",
                "message", ex.getMessage()
        ));
    }
}
