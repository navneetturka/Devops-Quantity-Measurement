package com.apps.quantitymeasurement.unit;

public enum VolumeUnit implements IMeasurable {

    LITRE(1.0),
    LITER(1.0),
    MILLILITRE(0.001),
    MILLILITER(0.001),
    GALLON(3.78541);

    private final double conversionFactor;

    VolumeUnit(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    @Override
    public double getConversionFactor() {
        return conversionFactor;
    }

    @Override
    public double convertToBaseUnit(double value) {
        return value * conversionFactor;
    }

    @Override
    public double convertFromBaseUnit(double baseValue) {
        return baseValue / conversionFactor;
    }

    @Override
    public String getUnitName() {
        return name();
    }

    public static VolumeUnit fromUnitName(String unitName) {
        return VolumeUnit.valueOf(unitName.toUpperCase());
    }
}