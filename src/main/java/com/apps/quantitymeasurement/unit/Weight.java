package com.apps.quantitymeasurement.unit;

public class Weight {
    private static final double EPSILON = 0.0001;
    private final double value;
    private final WeightUnit unit;

    public Weight(double value, WeightUnit unit) {

        if (unit == null) {
            throw new IllegalArgumentException(
                    "Unit cannot be null"
            );
        }

        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(
                    "Invalid value"
            );
        }

        this.value = value;
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public WeightUnit getUnit() {
        return unit;
    }

    private double convertToBaseUnit() {

        return unit.convertToBaseUnit(value);
    }
    public boolean compare(Weight other) {

        return Math.abs(
                this.convertToBaseUnit() -
                        other.convertToBaseUnit()
        ) < EPSILON;
    }
    public Weight convertTo(WeightUnit targetUnit) {

        if (targetUnit == null) {
            throw new IllegalArgumentException(
                    "Target unit cannot be null"
            );
        }

        double baseValue =
                this.convertToBaseUnit();

        double convertedValue =
                targetUnit.convertFromBaseUnit(
                        baseValue
                );

        return new Weight(
                convertedValue,
                targetUnit
        );
    }

    public Weight add(Weight thatWeight) {

        return add(
                thatWeight,
                this.unit
        );
    }

    public Weight add(
            Weight thatWeight,
            WeightUnit targetUnit
    ) {

        if (thatWeight == null) {
            throw new IllegalArgumentException(
                    "Weight cannot be null"
            );
        }

        if (targetUnit == null) {
            throw new IllegalArgumentException(
                    "Target unit cannot be null"
            );
        }

        if (thatWeight.unit == null) {
            throw new IllegalArgumentException(
                    "Unit cannot be null"
            );
        }

        if (!Double.isFinite(thatWeight.value)) {
            throw new IllegalArgumentException(
                    "Invalid value"
            );
        }

        double thisBaseValue =
                this.convertToBaseUnit();

        double thatBaseValue =
                thatWeight.convertToBaseUnit();

        double sumInBaseUnit =
                thisBaseValue + thatBaseValue;

        double resultValue =
                targetUnit.convertFromBaseUnit(
                        sumInBaseUnit
                );

        return new Weight(
                resultValue,
                targetUnit
        );
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;

        if (obj == null) return false;

        if (this.getClass() != obj.getClass()) return false;

        Weight other = (Weight) obj;

        return this.compare(other);
    }

    @Override
    public String toString() {

        return value + " " + unit;
    }
}