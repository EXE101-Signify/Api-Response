package fptu.exe202.signify.apiresponse.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for HTTP 401 Unauthorized.
 */
public class UnauthorizedException extends BaseException {

    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }

    public UnauthorizedException(String message, String errorCode) {
        super(HttpStatus.UNAUTHORIZED, message, errorCode);
    }
}
