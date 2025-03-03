// File: `src/test/java/com/example/quiz/exception/CustomExceptionsTest.java`
package unitTest.com.example.quiz.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class CustomExceptionsTest {

    @Test
    void testBadRequestExceptionMessage() {
        String message = "Bad Request error message";
        BadRequestException ex = new BadRequestException(message);
        assertEquals(message, ex.getMessage());

        // Verify that BadRequestException is annotated with the correct status
        ResponseStatus status = ex.getClass().getAnnotation(ResponseStatus.class);
        assertNotNull(status);
        assertEquals(HttpStatus.BAD_REQUEST, status.value());
    }

    @Test
    void testResourceNotFoundExceptionMessage() {
        String message = "Resource not found error message";
        ResourceNotFoundException ex = new ResourceNotFoundException(message);
        assertEquals(message, ex.getMessage());

        // Verify that ResourceNotFoundException is annotated with the correct status
        ResponseStatus status = ex.getClass().getAnnotation(ResponseStatus.class);
        assertNotNull(status);
        assertEquals(HttpStatus.NOT_FOUND, status.value());
    }

    @Test
    void testErrorResponseProperties() {
        int statusCode = 400;
        String message = "Error occurred";
        String path = "/test/path";

        ErrorResponse errorResponse = new ErrorResponse(statusCode, message, path);
        assertEquals(statusCode, errorResponse.getStatusCode());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(path, errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
        assertTrue(errorResponse.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testValidationErrorResponseProperties() {
        int statusCode = 400;
        String message = "Validation error";
        String path = "/test/validate";
        Map<String, String> errors = new HashMap<>();
        errors.put("field", "must not be empty");

        ValidationErrorResponse validationErrorResponse = new ValidationErrorResponse(statusCode, message, path, errors);
        assertEquals(statusCode, validationErrorResponse.getStatusCode());
        assertEquals(message, validationErrorResponse.getMessage());
        assertEquals(path, validationErrorResponse.getPath());
        assertEquals(errors, validationErrorResponse.getValidationErrors());
    }
}