package com.aof.mcinabox.network.model;

public class ErrorResponse {
    private String error;
    private String errorMessage;
    private String cause;

    public String getError() {
        return error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean hasCause() {
        return cause != null && cause.length() > 0;
    }

    public String getCause() {
        return cause;
    }
}
