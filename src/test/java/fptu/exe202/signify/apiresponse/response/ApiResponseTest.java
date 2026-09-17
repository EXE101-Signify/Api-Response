package fptu.exe202.signify.apiresponse.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    // ─────────────────────────── Success ──────────────────────────────────────

    @Nested
    @DisplayName("Success responses")
    class SuccessResponses {

        @Test
        @DisplayName("success(data) returns 200 with default message")
        void successWithData() {
            ApiResponse<String> response = ApiResponse.success("hello");

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getMessage()).isEqualTo("Success");
            assertThat(response.getData()).isEqualTo("hello");
            assertThat(response.getErrors()).isNull();
            assertThat(response.getTimestamp()).isNotNull();
            assertThat(response.getPath()).isNull();
        }

        @Test
        @DisplayName("success(message, data) returns 200 with custom message")
        void successWithMessageAndData() {
            ApiResponse<String> response = ApiResponse.success("Created", "user-1");

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getMessage()).isEqualTo("Created");
            assertThat(response.getData()).isEqualTo("user-1");
        }

        @Test
        @DisplayName("success with null data")
        void successWithNullData() {
            ApiResponse<Void> response = ApiResponse.success("Deleted", null);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getData()).isNull();
        }

        @Test
        @DisplayName("success response serializes to JSON correctly")
        void successSerializesToJson() throws Exception {
            ApiResponse<Map<String, String>> response =
                    ApiResponse.success(Map.of("name", "John"));

            String json = objectMapper.writeValueAsString(response);

            assertThat(json).contains("\"success\":true");
            assertThat(json).contains("\"status\":200");
            assertThat(json).contains("\"message\":\"Success\"");
            assertThat(json).contains("\"name\":\"John\"");
            // null fields should be omitted (JsonInclude.NON_NULL)
            assertThat(json).doesNotContain("\"errors\"");
            assertThat(json).doesNotContain("\"path\"");
        }
    }

    // ─────────────────────────── Error ────────────────────────────────────────

    @Nested
    @DisplayName("Error responses")
    class ErrorResponses {

        @Test
        @DisplayName("error(status, message) returns correct error response")
        void errorWithStatusAndMessage() {
            ApiResponse<Void> response = ApiResponse.error(404, "User not found");

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getStatus()).isEqualTo(404);
            assertThat(response.getMessage()).isEqualTo("User not found");
            assertThat(response.getData()).isNull();
            assertThat(response.getErrors()).isNull();
            assertThat(response.getTimestamp()).isNotNull();
        }

        @Test
        @DisplayName("error with validation errors")
        void errorWithValidationErrors() {
            Map<String, String> errors = new LinkedHashMap<>();
            errors.put("username", "must not be blank");
            errors.put("email", "must be a valid email");

            ApiResponse<Void> response = ApiResponse.error(400, "Validation failed", errors);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getStatus()).isEqualTo(400);
            assertThat(response.getMessage()).isEqualTo("Validation failed");
            assertThat(response.getErrors()).hasSize(2);
            assertThat(response.getErrors()).containsEntry("username", "must not be blank");
            assertThat(response.getErrors()).containsEntry("email", "must be a valid email");
        }

        @Test
        @DisplayName("error with path")
        void errorWithPath() {
            ApiResponse<Void> response = ApiResponse.error(
                    404, "Not found", "/api/v1/users/10");

            assertThat(response.getPath()).isEqualTo("/api/v1/users/10");
        }

        @Test
        @DisplayName("error with path and validation errors")
        void errorWithPathAndErrors() {
            Map<String, String> errors = Map.of("name", "required");

            ApiResponse<Void> response = ApiResponse.error(
                    400, "Validation failed", errors, "/api/v1/users");

            assertThat(response.getPath()).isEqualTo("/api/v1/users");
            assertThat(response.getErrors()).containsEntry("name", "required");
        }

        @Test
        @DisplayName("error response serializes to JSON correctly")
        void errorSerializesToJson() throws Exception {
            ApiResponse<Void> response = ApiResponse.error(
                    500, "Internal server error", "/api/v1/users");

            String json = objectMapper.writeValueAsString(response);

            assertThat(json).contains("\"success\":false");
            assertThat(json).contains("\"status\":500");
            assertThat(json).contains("\"message\":\"Internal server error\"");
            assertThat(json).contains("\"path\":\"/api/v1/users\"");
            // null data should be omitted
            assertThat(json).doesNotContain("\"data\"");
        }
    }
}
