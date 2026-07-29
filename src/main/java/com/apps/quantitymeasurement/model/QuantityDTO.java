package com.apps.quantitymeasurement.model;

import com.apps.quantitymeasurement.unit.IMeasurable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.logging.Logger;

@Data
@Schema(description = "A quantity with a value and unit")
public class QuantityDTO {

    private static final Logger logger =
            Logger.getLogger(QuantityDTO.class.getName());

    public interface IMeasurableUnit {
        String getUnitName();
        String getMeasurementType();
    }

    public enum LengthUnit implements IMeasurableUnit {
        FEET, INCHES, YARDS, CENTIMETERS, METER, METERS;
        @Override public String getUnitName()        { return name(); }
        @Override public String getMeasurementType() { return "LengthUnit"; }
    }

    public enum VolumeUnit implements IMeasurableUnit {
        LITRE, LITER, MILLILITRE, MILLILITER, GALLON;
        @Override public String getUnitName()        { return name(); }
        @Override public String getMeasurementType() { return "VolumeUnit"; }
    }

    public enum WeightUnit implements IMeasurableUnit {
        KILOGRAM, GRAM, POUND, OUNCE, TONNE;
        @Override public String getUnitName()        { return name(); }
        @Override public String getMeasurementType() { return "WeightUnit"; }
    }

    public enum TemperatureUnit implements IMeasurableUnit {
        CELSIUS, FAHRENHEIT, KELVIN;
        @Override public String getUnitName()        { return name(); }
        @Override public String getMeasurementType() { return "TemperatureUnit"; }
    }

    @NotNull(message = "Value cannot be empty")
    @Schema(example = "1.0")
    public double value;

    @NotNull(message = "Unit cannot be null")
    @Schema(example = "FEET")
    public String unit;

    @NotNull(message = "Measurement type cannot be null")
    @Pattern(
            regexp  = "LengthUnit|VolumeUnit|WeightUnit|TemperatureUnit",
            message = "Measurement type must be one of: LengthUnit, VolumeUnit, " +
                    "WeightUnit, TemperatureUnit"
    )
    @Schema(example = "LengthUnit",
            allowableValues = {"LengthUnit","VolumeUnit","WeightUnit","TemperatureUnit"})
    public String measurementType;

    public QuantityDTO() {}

    public QuantityDTO(double value, IMeasurableUnit unit) {
        this.value           = value;
        this.unit            = unit.getUnitName();
        this.measurementType = unit.getMeasurementType();
    }

    public QuantityDTO(double value, String unit, String measurementType) {
        this.value           = value;
        this.unit            = unit;
        this.measurementType = measurementType;
    }

    public QuantityDTO(double value, IMeasurable unit) {
        this.value           = value;
        this.unit            = unit.getUnitName();
        this.measurementType = unit.getMeasurementType();
    }

    @AssertTrue(message = "Unit must be valid for the specified measurement type")
    public boolean isValidUnit() {
        if (unit == null || measurementType == null) return true;
        try {
            switch (measurementType) {
                case "LengthUnit":
                    com.apps.quantitymeasurement.unit.LengthUnit
                            .fromUnitName(unit); break;
                case "VolumeUnit":
                    com.apps.quantitymeasurement.unit.VolumeUnit
                            .fromUnitName(unit); break;
                case "WeightUnit":
                    com.apps.quantitymeasurement.unit.WeightUnit
                            .fromUnitName(unit); break;
                case "TemperatureUnit":
                    com.apps.quantitymeasurement.unit.TemperatureUnit
                            .fromUnitName(unit); break;
                default: return false;
            }
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return value + " " + unit + " (" + measurementType + ")";
    }
}