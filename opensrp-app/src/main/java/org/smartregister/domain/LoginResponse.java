package org.smartregister.domain;

import org.smartregister.domain.jsonmapping.LoginResponseData;

public enum LoginResponse {
    SUCCESS("Login successful."),
    NO_INTERNET_CONNECTIVITY("No internet connection. Please ensure data connectivity"),
    MALFORMED_URL("Incorrect url"),
    UNKNOWN_RESPONSE("Dristhi login failed. Try later"),
    UNAUTHORIZED("Please check the credentials"),
    TIMEOUT("The server could not be reached. Try again"),
    EMPTY_REPONSE("The server returned an empty response. Try Again");

    private LoginResponseData payload;
    private String message;

    LoginResponse(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }

    public LoginResponseData payload() {
        return payload;
    }

    public LoginResponse withPayload(LoginResponseData payload) {
        this.payload = payload;
        return this;
    }
}
