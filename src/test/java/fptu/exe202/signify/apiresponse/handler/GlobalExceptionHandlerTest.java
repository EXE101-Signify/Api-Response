package fptu.exe202.signify.apiresponse.handler;

import fptu.exe202.signify.apiresponse.config.ApiResponseAutoConfiguration;
import fptu.exe202.signify.apiresponse.demo.DemoController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DemoController.class)
@ImportAutoConfiguration(ApiResponseAutoConfiguration.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    // ─────────────────────────── Success ──────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("GET /api/test/success → 200 with data")
    void success() throws Exception {
        mockMvc.perform(get("/api/test/success"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data").value("Hello, World!"));
    }

    // ─────────────────────────── Business Exceptions ─────────────────────────

    @Nested
    @DisplayName("Business exceptions")
    class BusinessExceptions {

        @Test
        @WithMockUser
        @DisplayName("ResourceNotFoundException → 404")
        void resourceNotFound() throws Exception {
            mockMvc.perform(get("/api/test/not-found"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("User not found"))
                    .andExpect(jsonPath("$.data").doesNotExist())
                    .andExpect(jsonPath("$.path").value("/api/test/not-found"))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());
        }

        @Test
        @WithMockUser
        @DisplayName("BadRequestException → 400")
        void badRequest() throws Exception {
            mockMvc.perform(get("/api/test/bad-request"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("Invalid request"))
                    .andExpect(jsonPath("$.path").value("/api/test/bad-request"));
        }

        @Test
        @WithMockUser
        @DisplayName("UnauthorizedException → 401")
        void unauthorized() throws Exception {
            mockMvc.perform(get("/api/test/unauthorized"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.message").value("Authentication required"))
                    .andExpect(jsonPath("$.path").value("/api/test/unauthorized"));
        }

        @Test
        @WithMockUser
        @DisplayName("ForbiddenException → 403")
        void forbidden() throws Exception {
            mockMvc.perform(get("/api/test/forbidden"))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.status").value(403))
                    .andExpect(jsonPath("$.message").value("Access denied"))
                    .andExpect(jsonPath("$.path").value("/api/test/forbidden"));
        }

        @Test
        @WithMockUser
        @DisplayName("ConflictException → 409")
        void conflict() throws Exception {
            mockMvc.perform(get("/api/test/conflict"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.message").value("Username already exists"))
                    .andExpect(jsonPath("$.path").value("/api/test/conflict"));
        }
    }

    // ─────────────────────────── Validation ───────────────────────────────────

    @Nested
    @DisplayName("Validation exceptions")
    class ValidationExceptions {

        @Test
        @WithMockUser
        @DisplayName("MethodArgumentNotValidException → 400 with field errors")
        void validationFailed() throws Exception {
            String body = """
                    {"username": "", "email": "not-an-email"}
                    """;

            mockMvc.perform(post("/api/test/validation")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.errors").isMap())
                    .andExpect(jsonPath("$.errors.username").isString())
                    .andExpect(jsonPath("$.errors.email").isString())
                    .andExpect(jsonPath("$.path").value("/api/test/validation"));
        }

        @Test
        @WithMockUser
        @DisplayName("HttpMessageNotReadableException → 400 malformed body")
        void malformedBody() throws Exception {
            mockMvc.perform(post("/api/test/validation")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{invalid json"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("Malformed request body"));
        }
    }

    // ─────────────────────────── Spring MVC Exceptions ───────────────────────

    @Nested
    @DisplayName("Spring MVC exceptions")
    class SpringMvcExceptions {

        @Test
        @WithMockUser
        @DisplayName("HttpRequestMethodNotSupportedException → 405")
        void methodNotAllowed() throws Exception {
            mockMvc.perform(delete("/api/test/success").with(csrf()))
                    .andExpect(status().isMethodNotAllowed())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.status").value(405))
                    .andExpect(jsonPath("$.message").value("Method 'DELETE' is not supported"));
        }
    }

    // ─────────────────────────── Unexpected Exception ────────────────────────

    @Nested
    @DisplayName("Unexpected exceptions")
    class UnexpectedExceptions {

        @Test
        @WithMockUser
        @DisplayName("RuntimeException → 500 without stack trace")
        void unexpectedException() throws Exception {
            mockMvc.perform(get("/api/test/exception"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.status").value(500))
                    .andExpect(jsonPath("$.message").value("Internal server error"))
                    .andExpect(jsonPath("$.data").doesNotExist())
                    .andExpect(jsonPath("$.path").value("/api/test/exception"));
        }
    }

    // ─────────────────────────── Security Exceptions ─────────────────────────

    @Nested
    @DisplayName("Spring Security exceptions")
    class SecurityExceptions {

        @Test
        @DisplayName("Unauthenticated request → 401 (handled by Spring Security filter)")
        void unauthenticatedRequest() throws Exception {
            // Without @WithMockUser, Spring Security rejects the request
            mockMvc.perform(get("/api/test/success"))
                    .andExpect(status().isUnauthorized());
        }
    }
}
