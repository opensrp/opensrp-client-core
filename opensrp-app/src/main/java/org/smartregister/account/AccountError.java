package org.smartregister.account;

import java.io.Serializable;

/**
 * Created by ndegwamartin on 2020-04-27.
 */
public class AccountError implements Serializable {

    private int statusCode;
    private String error;

    public AccountError(int statusCode, String error) {
        this.statusCode = statusCode;
        this.error = error;

    }
}
