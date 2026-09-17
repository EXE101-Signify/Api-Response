package fptu.exe202.signify.apiresponse.handler;

import fptu.exe202.signify.apiresponse.exception.BaseException;
import fptu.exe202.signify.apiresponse.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global exception handler that converts all exceptions into a unified {@link ApiResponse}.
 * <p>
 * Automatically registered via Spring Boot auto-configuration.
 * Handles business exceptions, validation errors, Spring MVC exceptions,
 * and optionally Spring Security exceptions.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ──────────────────────────── Business Exceptions ─────────────────────────

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(
            BaseException ex, HttpServletRequest request) {
        log.warn("Business exception: {} - {}", ex.getClass().getSimpleName(), ex.getMessage());
        HttpStatus status = ex.getStatus();
        ApiResponse<Void> body = ApiResponse.error(
                status.value(), ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }

    // ──────────────────────────── Validation Exceptions ───────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
        ApiResponse<Void> body = ApiResponse.error(
                HttpStatus.BAD_REQUEST.value(), "Validation failed", errors, request.getRequestURI());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String field = extractFieldName(violation.getPropertyPath().toString());
            errors.put(field, violation.getMessage());
        }
        ApiResponse<Void> body = ApiResponse.error(
                HttpStatus.BAD_REQUEST.value(), "Validation failed", errors, request.getRequestURI());
        return ResponseEntity.badRequest().body(body);
    }

    // ──────────────────────────── Spring MVC Exceptions ──────────────────────

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        ApiResponse<Void> body = ApiResponse.error(
                HttpStatus.BAD_REQUEST.value(), "Malformed request body", request.getRequestURI());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        String message = "Missing required parameter: " + ex.getParameterName();
        ApiResponse<Void> body = ApiResponse.error(
                HttpStatus.BAD_REQUEST.value(), message, request.getRequestURI());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingPathVariable(
            MissingPathVariableException ex, HttpServletRequest request) {
        String message = "Missing path variable: " + ex.getVariableName();
        ApiResponse<Void> body = ApiResponse.error(
                HttpStatus.BAD_REQUEST.value(), message, request.getRequestURI());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        String message = "Method '" + ex.getMethod() + "' is not supported";
        ApiResponse<Void> body = ApiResponse.error(
                HttpStatus.METHOD_NOT_ALLOWED.value(), message, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(body);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFound(
            NoResourceFoundException ex, HttpServletRequest request) {
        ApiResponse<Void> body = ApiResponse.error(
                HttpStatus.NOT_FOUND.value(), "Resource not found", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // ──────────────────────────── Fallback ────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(
            Exception ex, HttpServletRequest request) {
        log.error("Unexpected error on {} {}", request.getMethod(), request.getRequestURI(), ex);
        ApiResponse<Void> body = ApiResponse.error(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error",
                request.getRequestURI());
        return ResponseEntity.internalServerError().body(body);
    }

    // ──────────────────────────── Helpers ─────────────────────────────────────

    /**
     * Extracts the last segment of a property path (e.g., "createUser.email" → "email").
     */
    private static String extractFieldName(String propertyPath) {
        int lastDot = propertyPath.lastIndexOf('.');
        return lastDot >= 0 ? propertyPath.substring(lastDot + 1) : propertyPath;
    }
}
