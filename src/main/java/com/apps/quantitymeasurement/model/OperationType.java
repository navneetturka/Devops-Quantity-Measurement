package com.apps.quantitymeasurement.model;

public enum OperationType {
    ADD, SUBTRACT, MULTIPLY, DIVIDE, COMPARE, CONVERT;

    public static OperationType fromString(String value) {
        if (value == null) return null;
        try {
            return OperationType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}