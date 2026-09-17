package fptu.exe202.signify.apiresponse.demo;

import fptu.exe202.signify.apiresponse.exception.BadRequestException;
import fptu.exe202.signify.apiresponse.exception.ConflictException;
import fptu.exe202.signify.apiresponse.exception.ForbiddenException;
import fptu.exe202.signify.apiresponse.exception.ResourceNotFoundException;
import fptu.exe202.signify.apiresponse.exception.UnauthorizedException;
import fptu.exe202.signify.apiresponse.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test controller used in integration tests to exercise every exception path.
 * This class lives in src/test and is NOT published with the library.
 */
@RestController
@RequestMapping("/api/test")
public class DemoController {

    @GetMapping("/success")
    ApiResponse<String> success() {
        return ApiResponse.success("Hello, World!");
    }

    @GetMapping("/not-found")
    ApiResponse<Void> notFound() {
        throw new ResourceNotFoundException("User not found");
    }

    @GetMapping("/bad-request")
    ApiResponse<Void> badRequest() {
        throw new BadRequestException("Invalid request");
    }

    @GetMapping("/unauthorized")
    ApiResponse<Void> unauthorized() {
        throw new UnauthorizedException("Authentication required");
    }

    @GetMapping("/forbidden")
    ApiResponse<Void> forbidden() {
        throw new ForbiddenException("Access denied");
    }

    @GetMapping("/conflict")
    ApiResponse<Void> conflict() {
        throw new ConflictException("Username already exists");
    }

    @GetMapping("/exception")
    ApiResponse<Void> unexpectedException() {
        throw new RuntimeException("Something went terribly wrong");
    }

    @PostMapping("/validation")
    ApiResponse<String> validation(@Valid @RequestBody CreateUserRequest request) {
        return ApiResponse.success("User created", request.username());
    }

    // ─── Inner request DTO for validation testing ────────────────────────────

    record CreateUserRequest(
            @NotBlank(message = "Username must not be blank")
            String username,

            @Email(message = "Email must be valid")
            @NotBlank(message = "Email must not be blank")
            String email
    ) {
    }
}
