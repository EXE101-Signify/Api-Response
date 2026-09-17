package fptu.exe202.signify.apiresponse.exception;

import org.springframework.http.HttpStatus;

/**
 * Abstract base class for all business exceptions in the application.
 * <p>
 * Subclasses define the HTTP status and are automatically handled by
 * {@link fptu.exe202.signify.apiresponse.handler.GlobalExceptionHandler}.
 */
public abstract class BaseException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    protected BaseException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.errorCode = null;
    }

    protected BaseException(HttpStatus status, String message, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    protected BaseException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.errorCode = null;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
