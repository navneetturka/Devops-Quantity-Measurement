package com.apps.quantitymeasurement.unit;

public class Length {
    private static final double EPSILON = 0.000001;
    private final double value;
    private final LengthUnit unit;

    public Length(double value, LengthUnit unit) {

        if (unit == null) {
            throw new IllegalArgumentException("Unit cannot be null");
        }

        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Invalid value");
        }
        this.value = value;
        this.unit = unit;
    }
    public double getValue() {
        return value;
    }

    public LengthUnit getUnit() {
        return unit;
    }
    private double convertToBaseUnit() {
        return unit.convertToBaseUnit(value);
    }

    public boolean compare(Length other) {

        return Math.abs(
                this.convertToBaseUnit() -
                        other.convertToBaseUnit()
        ) < EPSILON;
    }
    public Length convertTo(LengthUnit targetUnit) {

        if (targetUnit == null) {
            throw new IllegalArgumentException("Target unit cannot be null");
        }

        double baseValue =
                this.convertToBaseUnit();

        double convertedValue =
                targetUnit.convertFromBaseUnit(baseValue);



        return new Length(convertedValue, targetUnit);
    }
    public Length add(Length thatLength) {

        return add(
                thatLength,
                this.unit
        );
    }
    public Length add(
            Length thatLength,
            LengthUnit targetUnit
    ) {

        if (thatLength == null) {
            throw new IllegalArgumentException(
                    "Length cannot be null"
            );
        }

        if (targetUnit == null) {
            throw new IllegalArgumentException(
                    "Target unit cannot be null"
            );
        }

        if (thatLength.unit == null) {
            throw new IllegalArgumentException(
                    "Unit cannot be null"
            );
        }

        if (!Double.isFinite(thatLength.value)) {
            throw new IllegalArgumentException(
                    "Invalid value"
            );
        }

        double thisBaseValue =
                this.convertToBaseUnit();

        double thatBaseValue =
                thatLength.convertToBaseUnit();

        double sumInBaseUnit =
                thisBaseValue + thatBaseValue;

        double resultValue =
                targetUnit.convertFromBaseUnit(sumInBaseUnit);

        return new Length(
                resultValue,
                targetUnit
        );
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;

        if (obj == null) return false;

        if (this.getClass() != obj.getClass()) return false;

        Length other = (Length) obj;

        return this.compare(other);
    }
    @Override
    public String toString() {

        return value + " " + unit;
    }
}