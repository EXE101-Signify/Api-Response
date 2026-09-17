package fptu.exe202.signify.apiresponse.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for HTTP 403 Forbidden.
 */
public class ForbiddenException extends BaseException {

    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }

    public ForbiddenException(String message, String errorCode) {
        super(HttpStatus.FORBIDDEN, message, errorCode);
    }
}
