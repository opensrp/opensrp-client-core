package org.smartregister.domain;

public enum LoginResponse {
    SUCCESS("Login successful."),
    NO_INTERNET_CONNECTIVITY("No internet connection. Please ensure data connectivity"),
    MALFORMED_URL("Incorrect url"),
    UNKNOWN_RESPONSE("Dristhi login failed. Try later"),
    UNAUTHORIZED("Please check the credentials");

    private String payload;
    private String message;

    LoginResponse(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }

    public String payload() {
        return payload;
    }

    public LoginResponse withPayload(String payload) {
        this.payload = payload;
        return this;
    }
}
