package com.apps.quantitymeasurement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name    = "quantity_measurement_entity",
        indexes = {
                @Index(name = "idx_operation", columnList = "operation"),
                @Index(name = "idx_measurement_type", columnList = "this_measurement_type"),
                @Index(name = "idx_created_at", columnList = "created_at")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantityMeasurementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "this_value", nullable = false)
    public double thisValue;

    @Column(name = "this_unit", nullable = false)
    public String thisUnit;

    @Column(name = "this_measurement_type", nullable = false)
    public String thisMeasurementType;

    @Column(name = "that_value", nullable = false)
    public double thatValue;

    @Column(name = "that_unit", nullable = false)
    public String thatUnit;

    @Column(name = "that_measurement_type", nullable = false)
    public String thatMeasurementType;

    @Column(name = "operation", nullable = false)
    public String operation;

    @Column(name = "result_value")
    public double resultValue;

    @Column(name = "result_unit")
    public String resultUnit;

    @Column(name = "result_measurement_type")
    public String resultMeasurementType;

    @Column(name = "result_string")
    public String resultString;

    @Column(name = "is_error")
    public boolean error;

    @Column(name = "error_message")
    public String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public QuantityMeasurementEntity(
            QuantityDTO thisQ, QuantityDTO thatQ,
            String operation, QuantityDTO result) {
        this.thisValue             = val(thisQ);
        this.thisUnit              = unit(thisQ);
        this.thisMeasurementType   = type(thisQ);
        this.thatValue             = val(thatQ);
        this.thatUnit              = unit(thatQ);
        this.thatMeasurementType   = type(thatQ);
        this.operation             = operation;
        this.resultValue           = result != null ? result.getValue() : 0.0;
        this.resultUnit            = result != null ? result.getUnit()   : null;
        this.resultMeasurementType = result != null
                ? result.getMeasurementType() : null;
        this.resultString          = null;
        this.error                 = false;
        this.errorMessage          = null;
    }

    public QuantityMeasurementEntity(
            QuantityDTO thisQ, String operation, QuantityDTO result) {
        this(thisQ, thisQ, operation, result);
        this.thatValue           = 0.0;
        this.thatUnit            = unit(thisQ);
        this.thatMeasurementType = type(thisQ);
    }

    public QuantityMeasurementEntity(
            QuantityDTO thisQ, QuantityDTO thatQ,
            String operation, String resultString) {
        this.thisValue             = val(thisQ);
        this.thisUnit              = unit(thisQ);
        this.thisMeasurementType   = type(thisQ);
        this.thatValue             = val(thatQ);
        this.thatUnit              = unit(thatQ);
        this.thatMeasurementType   = type(thatQ);
        this.operation             = operation;
        this.resultValue           = 0.0;
        this.resultUnit            = null;
        this.resultMeasurementType = null;
        this.resultString          = resultString;
        this.error                 = false;
        this.errorMessage          = null;
    }

    public QuantityMeasurementEntity(
            QuantityDTO thisQ, QuantityDTO thatQ,
            String operation, double resultValue) {
        this.thisValue             = val(thisQ);
        this.thisUnit              = unit(thisQ);
        this.thisMeasurementType   = type(thisQ);
        this.thatValue             = val(thatQ);
        this.thatUnit              = unit(thatQ);
        this.thatMeasurementType   = type(thatQ);
        this.operation             = operation;
        this.resultValue           = resultValue;
        this.resultUnit            = null;
        this.resultMeasurementType = null;
        this.resultString          = String.valueOf(resultValue);
        this.error                 = false;
        this.errorMessage          = null;
    }

    public QuantityMeasurementEntity(
            QuantityDTO thisQ, QuantityDTO thatQ,
            String operation, String errorMessage, boolean isError) {
        this.thisValue             = val(thisQ);
        this.thisUnit              = unit(thisQ);
        this.thisMeasurementType   = type(thisQ);
        this.thatValue             = val(thatQ);
        this.thatUnit              = unit(thatQ);
        this.thatMeasurementType   = type(thatQ);
        this.operation             = operation;
        this.resultValue           = 0.0;
        this.resultUnit            = null;
        this.resultMeasurementType = null;
        this.resultString          = null;
        this.error                 = isError;
        this.errorMessage          = errorMessage;
    }

    private static double val(QuantityDTO q)  { return q != null ? q.getValue()            : 0.0; }
    private static String unit(QuantityDTO q) { return q != null ? q.getUnit()              : ""; }
    private static String type(QuantityDTO q) { return q != null ? q.getMeasurementType()  : ""; }
}