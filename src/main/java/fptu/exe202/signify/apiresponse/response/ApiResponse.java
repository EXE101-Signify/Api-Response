package fptu.exe202.signify.apiresponse.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Unified API response wrapper for all REST endpoints.
 *
 * @param <T> the type of the response data
 */
@JsonPropertyOrder({"success", "status", "message", "data", "errors", "timestamp", "path"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ApiResponse<T> {

    private final boolean success;
    private final int status;
    private final String message;
    private final T data;
    private final Map<String, String> errors;
    private final OffsetDateTime timestamp;
    private final String path;

    private ApiResponse(boolean success, int status, String message,
                        T data, Map<String, String> errors, String path) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.data = data;
        this.errors = errors;
        this.timestamp = OffsetDateTime.now();
        this.path = path;
    }

    // ──────────────────────────── Success factories ────────────────────────────

    /**
     * Creates a success response with data and default message.
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, 200, "Success", data, null, null);
    }

    /**
     * Creates a success response with a custom message and data.
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, 200, message, data, null, null);
    }

    // ──────────────────────────── Error factories ─────────────────────────────

    /**
     * Creates an error response without field-level errors.
     */
    public static ApiResponse<Void> error(int status, String message) {
        return new ApiResponse<>(false, status, message, null, null, null);
    }

    /**
     * Creates an error response with field-level validation errors.
     */
    public static ApiResponse<Void> error(int status, String message,
                                          Map<String, String> errors) {
        return new ApiResponse<>(false, status, message, null, errors, null);
    }

    // ──────────────────── Internal builder for handler use ────────────────────

    /**
     * Creates an error response with path information (used by GlobalExceptionHandler).
     */
    public static ApiResponse<Void> error(int status, String message, String path) {
        return new ApiResponse<>(false, status, message, null, null, path);
    }

    /**
     * Creates an error response with path and field-level errors (used by GlobalExceptionHandler).
     */
    public static ApiResponse<Void> error(int status, String message,
                                          Map<String, String> errors, String path) {
        return new ApiResponse<>(false, status, message, null, errors, path);
    }

    // ──────────────────────────── Getters ─────────────────────────────────────

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }
}
