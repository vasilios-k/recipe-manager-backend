package de.htw.berlin.webtech.recipe_manager.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;

/**
 * Globale Fehlerbehandlung für saubere, einheitliche JSON-Fehlerantworten.
 * Jeder Handler wandelt eine Exception in (status, body) um.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Respektiere ResponseStatusException exakt (z. B. 404).
     * Nachricht aus ex.getReason() oder Fallback auf Standardtext.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(
            ResponseStatusException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;
        String msg = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        return resp(status, msg, request, null);
    }

    /**
     * 400 – Bean Validation (z. B. @Valid im Controller schlug fehl).
     * Extrahiert Feldname + Meldung pro Verstoß in ein Array "violations".
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<Map<String, String>> violations = new ArrayList<>();
        for (var err : ex.getBindingResult().getAllErrors()) {
            String field = (err instanceof FieldError fe) ? fe.getField() : err.getObjectName();
            String msg = err.getDefaultMessage();
            violations.add(Map.of("field", field, "message", msg));
        }
        return resp(HttpStatus.BAD_REQUEST, "Validation failed", request, Map.of("violations", violations));
    }

    /**
     * 400 – JSON nicht lesbar (z. B. Syntaxfehler).
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        return resp(HttpStatus.BAD_REQUEST, "Malformed JSON request", request, null);
    }

    /**
     * 400 – fachliche Fehler aus dem Code (IllegalArgumentException).
     * z. B. Baseline-Regel verletzt.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        return resp(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
    }

    /**
     * 400/500 – transaktionale Wrapper (z. B. Bean Validation tief in der Persistence).
     * Versucht, die eigentliche IllegalArgumentException freizulegen.
     */
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Map<String, Object>> handleTxSystem(
            TransactionSystemException ex, HttpServletRequest request) {
        Throwable root = ex.getMostSpecificCause();
        if (root instanceof IllegalArgumentException iae) {
            return resp(HttpStatus.BAD_REQUEST, iae.getMessage(), request, null);
        }
        return resp(HttpStatus.INTERNAL_SERVER_ERROR, "Transaction error", request, null);
    }

    /**
     * 409 – Datenbankkonflikte (z. B. Unique Constraint verletzt).
     * Gibt die Root-Cause-Message im Body aus (hilft beim Debuggen).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        String detail = ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage()
                : "Data integrity violation";
        return resp(HttpStatus.CONFLICT, detail, request, null);
    }

    /**
     * 500 – Fallback für alles Unerwartete.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAny(
            Exception ex, HttpServletRequest request) {
        return resp(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", request, null);
    }

    // Hilfsmethode: standardisierte Fehlerantwort
    private ResponseEntity<Map<String, Object>> resp(HttpStatus status, String message, HttpServletRequest req, Map<String, ?> extra) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", req.getRequestURI());
        if (extra != null) body.putAll(extra);
        return ResponseEntity.status(status).body(body);
    }
}
