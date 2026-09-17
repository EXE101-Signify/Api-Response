package fptu.exe202.signify.apiresponse.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public ResourceNotFoundException(String message, String errorCode) {
        super(HttpStatus.NOT_FOUND, message, errorCode);
    }
}
