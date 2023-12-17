package org.dudinskiy.giftcert.exception;

import org.springframework.dao.DataAccessException;

public class DaoException extends DataAccessException {

    private final String errorMessage;
    private final String errorCode;

    public DaoException(String errorCode, String msg, Throwable cause) {
        super(msg, cause);
        this.errorCode = errorCode;
        this.errorMessage = msg;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "Error occurred: " + getErrorCode() + ": " + getErrorMessage();
    }
}
