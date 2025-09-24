package com.example.franchise.infrastructure.adapter.rest;

import com.example.franchise.application.usecase.errors.DomainErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(DomainErrors.NotFound.class)
    public ResponseEntity<?> notFound(RuntimeException ex) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("NOT_FOUND", ex.getMessage())); }

    @ExceptionHandler(DomainErrors.Conflict.class)
    public ResponseEntity<?> conflict(RuntimeException ex) { return ResponseEntity.status(HttpStatus.CONFLICT).body(error("CONFLICT", ex.getMessage())); }

    @ExceptionHandler(DomainErrors.Validation.class)
    public ResponseEntity<?> validation(RuntimeException ex) { return ResponseEntity.badRequest().body(error("VALIDATION", ex.getMessage())); }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> beanValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream().findFirst().map(fe -> fe.getField()+": "+fe.getDefaultMessage()).orElse("Validation error");
        return ResponseEntity.badRequest().body(error("VALIDATION", msg));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> generic(Exception ex) { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error("ERROR", ex.getMessage())); }

    private Map<String,Object> error(String code, String message) { return Map.of("code", code, "message", message); }
}
