package com.example.quiz.exception;

import java.util.Map;

public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> validationErrors;

    public ValidationErrorResponse(int statusCode, String message, String path, Map<String, String> validationErrors) {
        super(statusCode, message, path);
        this.validationErrors = validationErrors;
    }

    // Getters and Setters

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
