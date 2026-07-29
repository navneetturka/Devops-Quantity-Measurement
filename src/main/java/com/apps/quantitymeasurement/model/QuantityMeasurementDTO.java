package com.apps.quantitymeasurement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class QuantityMeasurementDTO {

    public Long id;
    public LocalDateTime createdAt;

    public double thisValue;
    public String thisUnit;
    public String thisMeasurementType;

    public double thatValue;
    public String thatUnit;
    public String thatMeasurementType;

    public String operation;
    public String resultString;
    public double resultValue;
    public String resultUnit;
    public String resultMeasurementType;
    public String errorMessage;

    @JsonProperty("error")
    public boolean error;

    public static QuantityMeasurementDTO from(QuantityMeasurementEntity entity) {
        if (entity == null) return null;
        QuantityMeasurementDTO dto = new QuantityMeasurementDTO();
        dto.id                     = entity.getId();
        dto.createdAt              = entity.getCreatedAt();
        dto.thisValue              = entity.getThisValue();
        dto.thisUnit               = entity.getThisUnit();
        dto.thisMeasurementType    = entity.getThisMeasurementType();
        dto.thatValue              = entity.getThatValue();
        dto.thatUnit               = entity.getThatUnit();
        dto.thatMeasurementType    = entity.getThatMeasurementType();
        dto.operation              = entity.getOperation();
        dto.resultString           = entity.getResultString();
        dto.resultValue            = entity.getResultValue();
        dto.resultUnit             = entity.getResultUnit();
        dto.resultMeasurementType  = entity.getResultMeasurementType();
        dto.errorMessage           = entity.getErrorMessage();
        dto.error                  = entity.isError();
        return dto;
    }

    public QuantityMeasurementEntity toEntity() {
        QuantityMeasurementEntity e = new QuantityMeasurementEntity();
        e.setThisValue(thisValue);
        e.setThisUnit(thisUnit);
        e.setThisMeasurementType(thisMeasurementType);
        e.setThatValue(thatValue);
        e.setThatUnit(thatUnit);
        e.setThatMeasurementType(thatMeasurementType);
        e.setOperation(operation);
        e.setResultString(resultString);
        e.setResultValue(resultValue);
        e.setResultUnit(resultUnit);
        e.setResultMeasurementType(resultMeasurementType);
        e.setErrorMessage(errorMessage);
        e.setError(error);
        return e;
    }

    public static List<QuantityMeasurementDTO> fromList(
            List<QuantityMeasurementEntity> entities) {
        return entities.stream()
                .map(QuantityMeasurementDTO::from)
                .collect(Collectors.toList());
    }

    public static List<QuantityMeasurementEntity> toEntityList(
            List<QuantityMeasurementDTO> dtos) {
        return dtos.stream()
                .map(QuantityMeasurementDTO::toEntity)
                .collect(Collectors.toList());
    }
}