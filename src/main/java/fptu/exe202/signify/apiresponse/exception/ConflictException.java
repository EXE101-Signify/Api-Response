package fptu.exe202.signify.apiresponse.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for HTTP 409 Conflict.
 */
public class ConflictException extends BaseException {

    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }

    public ConflictException(String message, String errorCode) {
        super(HttpStatus.CONFLICT, message, errorCode);
    }
}
