package com.apps.quantitymeasurement.unit;

import com.apps.quantitymeasurement.exception.QuantityMeasurementException;

public enum TemperatureUnit implements IMeasurable {

    CELSIUS,
    FAHRENHEIT,
    KELVIN;

    @Override
    public double getConversionFactor() {
        return 1.0;
    }

    @Override
    public double convertToBaseUnit(double value) {
        switch (this) {
            case CELSIUS:
                return value;
            case FAHRENHEIT:
                return (value - 32) * 5 / 9;
            case KELVIN:
                return value - 273.15;
            default:
                throw new IllegalArgumentException("Unsupported temperature unit");
        }
    }

    @Override
    public double convertFromBaseUnit(double baseValue) {
        switch (this) {
            case CELSIUS:
                return baseValue;
            case FAHRENHEIT:
                return (baseValue * 9 / 5) + 32;
            case KELVIN:
                return baseValue + 273.15;
            default:
                throw new IllegalArgumentException("Unsupported temperature unit");
        }
    }

    @Override
    public boolean supportsArithmetic() {
        return false;
    }

    @Override
    public void validateOperationSupport(String operation) {
        if ("DIVIDE".equalsIgnoreCase(operation)) {
            throw new QuantityMeasurementException(
                    "Temperature does not support division");
        }
    }

    @Override
    public String getUnitName() {
        return name();
    }

    public static TemperatureUnit fromUnitName(String unitName) {
        return TemperatureUnit.valueOf(unitName.toUpperCase());
    }
}