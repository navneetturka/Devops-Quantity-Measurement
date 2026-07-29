package com.apps.quantitymeasurement.service;

import com.apps.quantitymeasurement.model.QuantityDTO;
import com.apps.quantitymeasurement.model.QuantityMeasurementDTO;

import java.util.List;

public interface IQuantityMeasurementService {

    QuantityMeasurementDTO compare(
            QuantityDTO thisQuantityDTO,
            QuantityDTO thatQuantityDTO);

    QuantityMeasurementDTO convert(
            QuantityDTO thisQuantityDTO,
            QuantityDTO thatQuantityDTO);

    QuantityMeasurementDTO add(
            QuantityDTO thisQuantityDTO,
            QuantityDTO thatQuantityDTO);

    QuantityMeasurementDTO add(
            QuantityDTO thisQuantityDTO,
            QuantityDTO thatQuantityDTO,
            QuantityDTO targetUnitDTO);

    QuantityMeasurementDTO subtract(
            QuantityDTO thisQuantityDTO,
            QuantityDTO thatQuantityDTO);

    QuantityMeasurementDTO subtract(
            QuantityDTO thisQuantityDTO,
            QuantityDTO thatQuantityDTO,
            QuantityDTO targetUnitDTO);

    QuantityMeasurementDTO divide(
            QuantityDTO thisQuantityDTO,
            QuantityDTO thatQuantityDTO);

    List<QuantityMeasurementDTO> getOperationHistory(String operation);

    List<QuantityMeasurementDTO> getMeasurementsByType(String type);

    long getOperationCount(String operation);

    List<QuantityMeasurementDTO> getErrorHistory();

    void deleteHistoryEntry(Long id);

    void clearAllHistory();
}