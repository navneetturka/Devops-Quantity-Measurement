package com.apps.quantitymeasurement.quantity;

import com.apps.quantitymeasurement.unit.IMeasurable;
import java.util.Objects;

public class Quantity<U extends IMeasurable> {

    private static final double EPSILON = 0.0001;

    private final double value;
    private final U unit;

    public Quantity(double value, U unit) {
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

    public U getUnit() {
        return unit;
    }

    private double convertToBaseUnit() {
        return unit.convertToBaseUnit(value);
    }

    public boolean compare(Quantity<U> other) {
        if (other == null) {
            throw new IllegalArgumentException("Quantity cannot be null");
        }

        return Math.abs(
                this.convertToBaseUnit() - other.convertToBaseUnit()
        ) < EPSILON;
    }

    public Quantity<U> convertTo(U targetUnit) {
        if (targetUnit == null) {
            throw new IllegalArgumentException("Target unit cannot be null");
        }

        double baseValue = convertToBaseUnit();
        double convertedValue = targetUnit.convertFromBaseUnit(baseValue);

        return new Quantity<>(convertedValue, targetUnit);
    }

    public Quantity<U> add(Quantity<U> other) {
        return add(other, this.unit);
    }

    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        return arithmetic(other, targetUnit, ArithmeticOperation.ADD);
    }

    public Quantity<U> subtract(Quantity<U> other) {
        return subtract(other, this.unit);
    }

    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
        return arithmetic(other, targetUnit, ArithmeticOperation.SUBTRACT);
    }

    public double divide(Quantity<U> other) {
        if (other == null) {
            throw new IllegalArgumentException("Quantity cannot be null");
        }

        validateCompatibleUnit(other.unit);

        unit.validateOperationSupport("DIVIDE");
        other.unit.validateOperationSupport("DIVIDE");

        double divisor = other.convertToBaseUnit();

        if (Math.abs(divisor) < EPSILON) {
            throw new ArithmeticException("Cannot divide by zero");
        }

        return convertToBaseUnit() / divisor;
    }

    private Quantity<U> arithmetic(
            Quantity<U> other,
            U targetUnit,
            ArithmeticOperation operation
    ) {
        if (other == null) {
            throw new IllegalArgumentException("Quantity cannot be null");
        }

        if (targetUnit == null) {
            throw new IllegalArgumentException("Target unit cannot be null");
        }

        validateCompatibleUnit(other.unit);
        validateCompatibleUnit(targetUnit);

        unit.validateOperationSupport(operation.name());
        other.unit.validateOperationSupport(operation.name());
        targetUnit.validateOperationSupport(operation.name());

        double resultBaseValue =
                operation.compute(
                        convertToBaseUnit(),
                        other.convertToBaseUnit()
                );

        return new Quantity<>(
                targetUnit.convertFromBaseUnit(resultBaseValue),
                targetUnit
        );
    }

    private void validateCompatibleUnit(U otherUnit) {
        if (unit.getClass() != otherUnit.getClass()) {
            throw new IllegalArgumentException("Incompatible unit types");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Quantity<?>)) return false;

        Quantity<?> other = (Quantity<?>) obj;

        if (unit.getClass() != other.unit.getClass()) {
            return false;
        }

        return Math.abs(
                convertToBaseUnit()
                        - other.unit.convertToBaseUnit(other.value)
        ) < EPSILON;
    }

    @Override
    public int hashCode() {
        return Objects.hash(convertToBaseUnit(), unit.getClass());
    }

    @Override
    public String toString() {
        return value + " " + unit.getUnitName();
    }

    private enum ArithmeticOperation {
        ADD {
            double compute(double first, double second) {
                return first + second;
            }
        },

        SUBTRACT {
            double compute(double first, double second) {
                return first - second;
            }
        };

        abstract double compute(double first, double second);
    }
}