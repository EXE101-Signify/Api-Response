package fptu.exe202.signify.apiresponse.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for HTTP 400 Bad Request.
 */
public class BadRequestException extends BaseException {

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public BadRequestException(String message, String errorCode) {
        super(HttpStatus.BAD_REQUEST, message, errorCode);
    }
}
