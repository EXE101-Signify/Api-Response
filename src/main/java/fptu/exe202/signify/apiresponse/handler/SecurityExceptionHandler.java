package fptu.exe202.signify.apiresponse.handler;

import fptu.exe202.signify.apiresponse.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handles Spring Security exceptions when Spring Security is on the classpath.
 * <p>
 * This class is conditionally registered by
 * {@link fptu.exe202.signify.apiresponse.config.ApiResponseAutoConfiguration}.
 * If Spring Security is not present, this handler is simply not loaded.
 */
@RestControllerAdvice
public class SecurityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(SecurityExceptionHandler.class);

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthentication(
            AuthenticationException ex, HttpServletRequest request) {
        log.warn("Authentication failed: {}", ex.getMessage());
        ApiResponse<Void> body = ApiResponse.error(
                HttpStatus.UNAUTHORIZED.value(), "Unauthorized", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied: {}", ex.getMessage());
        ApiResponse<Void> body = ApiResponse.error(
                HttpStatus.FORBIDDEN.value(), "Forbidden", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }
}
