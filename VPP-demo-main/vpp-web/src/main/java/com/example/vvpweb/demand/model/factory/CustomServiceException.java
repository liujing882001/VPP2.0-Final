package com.example.vvpweb.demand.model.factory;

public class CustomServiceException extends RuntimeException {
    private String requestID;

    public CustomServiceException(String message, Throwable cause, String requestID) {
        super(message, cause);
        this.requestID = requestID;
    }

    public String getRequestID() {
        return requestID;
    }
}

