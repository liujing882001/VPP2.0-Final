package com.example.vvpcommom.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Unified API Response Wrapper
 * Provides a standardized structure for all API responses
 *
 * @param <T> Type of the response data
 */
@Data
public class ApiResponse<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Response status code
     */
    private int code;

    /**
     * Response message
     */
    private String message;

    /**
     * Response data
     */
    private T data;

    /**
     * Response timestamp
     */
    private LocalDateTime timestamp;

    /**
     * Default constructor
     */
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructor with data
     */
    public ApiResponse(T data) {
        this();
        this.code = HttpStatus.OK.value();
        this.message = "Success";
        this.data = data;
    }

    /**
     * Constructor with custom message
     */
    public ApiResponse(String message) {
        this();
        this.code = HttpStatus.OK.value();
        this.message = message;
    }

    /**
     * Constructor with custom code and message
     */
    public ApiResponse(int code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    /**
     * Create a success response
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data);
    }

    /**
     * Create a success response with message
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(message);
    }

    /**
     * Create an error response
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
    }

    /**
     * Create an error response with custom status
     */
    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return new ApiResponse<>(status.value(), message);
    }
} 