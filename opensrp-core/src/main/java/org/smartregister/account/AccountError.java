package org.smartregister.account;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ndegwamartin on 2020-04-27.
 */
public class AccountError implements Serializable {

    public static final String ACCOUNT_NOT_FULLY_SETUP = "Account is not fully set up";
    public static final String INVALID_CLIENT = "invalid_client";

    @SerializedName("status_code")
    private int statusCode;
    private String error;
    @SerializedName("error_description")
    private String errorDescription;

    public AccountError(int statusCode, String error) {
        this.statusCode = statusCode;
        this.error = error;

    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getError() {
        return error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }
}
