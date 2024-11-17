package com.example.quiz.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private int statusCode;
    private String message;
    private String path;
    private final LocalDateTime timestamp;

    public ErrorResponse(int statusCode, String message, String path) {
        this.statusCode = statusCode;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Typically, the timestamp is set when the error response is created,
    // so you might not need a setter for it. If required, you can uncomment the setter below.

    // public void setTimestamp(LocalDateTime timestamp) {
    //     this.timestamp = timestamp;
    // }
}
