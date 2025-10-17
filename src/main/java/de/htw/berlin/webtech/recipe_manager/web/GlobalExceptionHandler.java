package de.htw.berlin.webtech.recipe_manager.web;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 400: Bean Validation (@Valid) Fehler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<Map<String, String>> violations = new ArrayList<>();
        for (var err : ex.getBindingResult().getAllErrors()) {
            String field = (err instanceof FieldError fe) ? fe.getField() : err.getObjectName();
            String msg = err.getDefaultMessage();
            violations.add(Map.of("field", field, "message", msg));
        }

        Map<String, Object> body = baseBody(HttpStatus.BAD_REQUEST, "Validation failed", request);
        body.put("violations", violations);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 400: fachliche Fehler (z. B. Baseline-Regel)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        Map<String, Object> body = baseBody(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 409: DB-Konflikte (Unique-Constraints etc.)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        Map<String, Object> body = baseBody(HttpStatus.CONFLICT, "Data integrity violation", request);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // 500: Fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAny(
            Exception ex, HttpServletRequest request) {
        Map<String, Object> body = baseBody(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private Map<String, Object> baseBody(HttpStatus status, String message, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getRequestURI());
        return body;
    }
}
