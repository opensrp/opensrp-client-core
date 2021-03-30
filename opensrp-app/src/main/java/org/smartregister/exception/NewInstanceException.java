package org.smartregister.exception;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 24-09-2020
 */

public class NewInstanceException extends RuntimeException {

    public NewInstanceException() {
    }

    public NewInstanceException(String message) {
        super(message);
    }
}
