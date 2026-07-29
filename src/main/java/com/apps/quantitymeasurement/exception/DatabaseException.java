package com.apps.quantitymeasurement.exception;

public class DatabaseException extends QuantityMeasurementException {

    private static final long serialVersionUID = 1L;

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}