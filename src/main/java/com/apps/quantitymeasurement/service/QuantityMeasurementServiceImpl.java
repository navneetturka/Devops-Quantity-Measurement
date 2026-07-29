package com.apps.quantitymeasurement.service;

import com.apps.quantitymeasurement.model.QuantityDTO;
import com.apps.quantitymeasurement.model.QuantityMeasurementDTO;
import com.apps.quantitymeasurement.model.QuantityMeasurementEntity;
import com.apps.quantitymeasurement.model.QuantityModel;
import com.apps.quantitymeasurement.exception.QuantityMeasurementException;
import com.apps.quantitymeasurement.repository.QuantityMeasurementRepository;
import com.apps.quantitymeasurement.unit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.DoubleBinaryOperator;
import java.util.logging.Logger;

@Service
public class QuantityMeasurementServiceImpl
        implements IQuantityMeasurementService {

    private static final Logger logger =
            Logger.getLogger(QuantityMeasurementServiceImpl.class.getName());

    private static final double EPSILON = 0.0001;

    @Autowired
    QuantityMeasurementRepository repository;

    @Override
    public QuantityMeasurementDTO compare(
            QuantityDTO thisDTO, QuantityDTO thatDTO) {

        try {
            QuantityModel<IMeasurable> thisModel = convertDtoToModel(thisDTO);
            QuantityModel<IMeasurable> thatModel = convertDtoToModel(thatDTO);

            boolean sameType = isSameType(
                    thisModel.getUnit(), thatModel.getUnit());

            boolean result = sameType && Math.abs(
                    thisModel.getUnit().convertToBaseUnit(thisModel.getValue())
                            - thatModel.getUnit().convertToBaseUnit(thatModel.getValue())
            ) < EPSILON;

            QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
                    thisDTO, thatDTO, "compare", String.valueOf(result));
            repository.save(entity);

            QuantityMeasurementDTO dto = QuantityMeasurementDTO.from(entity);
            dto.setResultString(String.valueOf(result));
            return dto;

        } catch (RuntimeException e) {
            saveError(thisDTO, thatDTO, "compare", e.getMessage());
            throw e;
        }
    }

    @Override
    public QuantityMeasurementDTO convert(
            QuantityDTO thisDTO, QuantityDTO thatDTO) {

        try {
            QuantityModel<IMeasurable> thisModel = convertDtoToModel(thisDTO);
            QuantityModel<IMeasurable> targetModel = convertDtoToModel(thatDTO);

            validateSameType(thisModel.getUnit(), targetModel.getUnit());

            double base = thisModel.getUnit()
                    .convertToBaseUnit(thisModel.getValue());

            double converted = targetModel.getUnit()
                    .convertFromBaseUnit(base);

            QuantityDTO resultDTO = new QuantityDTO(
                    converted,
                    targetModel.getUnit().getUnitName(),
                    targetModel.getUnit().getMeasurementType()
            );

            QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
                    thisDTO, thatDTO, "convert", resultDTO);

            repository.save(entity);
            return QuantityMeasurementDTO.from(entity);

        } catch (RuntimeException e) {
            saveError(thisDTO, thatDTO, "convert", e.getMessage());
            throw e;
        }
    }

    @Override
    public QuantityMeasurementDTO add(
            QuantityDTO thisDTO, QuantityDTO thatDTO) {
        return add(thisDTO, thatDTO, thisDTO);
    }

    @Override
    public QuantityMeasurementDTO add(
            QuantityDTO thisDTO, QuantityDTO thatDTO, QuantityDTO targetDTO) {

        try {
            QuantityDTO result = arithmetic(
                    thisDTO, thatDTO, targetDTO,
                    (a, b) -> a + b, "ADD");

            QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
                    thisDTO, thatDTO, "add", result);

            repository.save(entity);
            return QuantityMeasurementDTO.from(entity);

        } catch (RuntimeException e) {
            saveError(thisDTO, thatDTO, "add", e.getMessage());
            throw e;
        }
    }

    @Override
    public QuantityMeasurementDTO subtract(
            QuantityDTO thisDTO, QuantityDTO thatDTO) {
        return subtract(thisDTO, thatDTO, thisDTO);
    }

    @Override
    public QuantityMeasurementDTO subtract(
            QuantityDTO thisDTO, QuantityDTO thatDTO, QuantityDTO targetDTO) {

        try {
            QuantityDTO result = arithmetic(
                    thisDTO, thatDTO, targetDTO,
                    (a, b) -> a - b, "SUBTRACT");

            QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
                    thisDTO, thatDTO, "subtract", result);

            repository.save(entity);
            return QuantityMeasurementDTO.from(entity);

        } catch (RuntimeException e) {
            saveError(thisDTO, thatDTO, "subtract", e.getMessage());
            throw e;
        }
    }

    @Override
    public QuantityMeasurementDTO divide(
            QuantityDTO thisDTO, QuantityDTO thatDTO) {

        try {
            QuantityModel<IMeasurable> thisModel = convertDtoToModel(thisDTO);
            QuantityModel<IMeasurable> thatModel = convertDtoToModel(thatDTO);

            validateSameType(thisModel.getUnit(), thatModel.getUnit());

            thisModel.getUnit().validateOperationSupport("DIVIDE");
            thatModel.getUnit().validateOperationSupport("DIVIDE");

            double thisBase = thisModel.getUnit()
                    .convertToBaseUnit(thisModel.getValue());

            double thatBase = thatModel.getUnit()
                    .convertToBaseUnit(thatModel.getValue());

            if (Math.abs(thatBase) < EPSILON) {
                throw new QuantityMeasurementException("Divide by zero");
            }

            double ratio = thisBase / thatBase;

            QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
                    thisDTO, thatDTO, "divide", ratio);

            repository.save(entity);
            return QuantityMeasurementDTO.from(entity);

        } catch (RuntimeException e) {
            saveError(thisDTO, thatDTO, "divide", e.getMessage());
            throw e;
        }
    }

    // ── history ─────────────────────────────────────────

    @Override
    public List<QuantityMeasurementDTO> getOperationHistory(String operation) {
        return QuantityMeasurementDTO.fromList(
                repository.findByOperation(operation.toLowerCase()));
    }

    @Override
    public List<QuantityMeasurementDTO> getMeasurementsByType(String type) {
        return QuantityMeasurementDTO.fromList(
                repository.findByThisMeasurementType(type));
    }

    @Override
    public long getOperationCount(String operation) {
        return repository.countByOperationAndErrorFalse(
                operation.toLowerCase());
    }

    @Override
    public List<QuantityMeasurementDTO> getErrorHistory() {
        return QuantityMeasurementDTO.fromList(
                repository.findByErrorTrue());
    }

    @Override
    public void deleteHistoryEntry(Long id) {
        if (!repository.existsById(id)) {
            throw new QuantityMeasurementException(
                    "History record not found: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public void clearAllHistory() {
        repository.deleteAll();
    }

    // ── helpers ─────────────────────────────────────────

    private void saveError(
            QuantityDTO thisDTO, QuantityDTO thatDTO,
            String operation, String errorMessage) {

        try {
            repository.save(new QuantityMeasurementEntity(
                    thisDTO, thatDTO, operation, errorMessage, true));
        } catch (Exception ex) {
            logger.warning("Failed to save error: " + ex.getMessage());
        }
    }

    private QuantityDTO arithmetic(
            QuantityDTO thisDTO, QuantityDTO thatDTO, QuantityDTO targetDTO,
            DoubleBinaryOperator op, String opName) {

        QuantityModel<IMeasurable> a = convertDtoToModel(thisDTO);
        QuantityModel<IMeasurable> b = convertDtoToModel(thatDTO);
        QuantityModel<IMeasurable> t = convertDtoToModel(targetDTO);

        validateSameType(a.getUnit(), b.getUnit());
        validateSameType(a.getUnit(), t.getUnit());

        a.getUnit().validateOperationSupport(opName);
        b.getUnit().validateOperationSupport(opName);
        t.getUnit().validateOperationSupport(opName);

        double resultBase = op.applyAsDouble(
                a.getUnit().convertToBaseUnit(a.getValue()),
                b.getUnit().convertToBaseUnit(b.getValue())
        );

        double resultVal = t.getUnit().convertFromBaseUnit(resultBase);

        return new QuantityDTO(
                resultVal,
                t.getUnit().getUnitName(),
                t.getUnit().getMeasurementType()
        );
    }

    private QuantityModel<IMeasurable> convertDtoToModel(QuantityDTO dto) {
        if (dto == null) {
            throw new QuantityMeasurementException("Quantity cannot be null");
        }

        IMeasurable unit = resolveUnit(
                dto.getMeasurementType(), dto.getUnit());

        return new QuantityModel<>(dto.getValue(), unit);
    }

    private IMeasurable resolveUnit(String type, String unit) {

        switch (type.toLowerCase()) {

            case "lengthunit":
                return LengthUnit.fromUnitName(unit);

            case "weightunit":
                return WeightUnit.fromUnitName(unit);

            case "volumeunit":
                return VolumeUnit.fromUnitName(unit);

            case "temperatureunit":
                return TemperatureUnit.fromUnitName(unit);

            default:
                throw new QuantityMeasurementException(
                        "Unsupported type: " + type);
        }
    }

    private boolean isSameType(IMeasurable a, IMeasurable b) {
        return a.getClass() == b.getClass();
    }

    private void validateSameType(IMeasurable a, IMeasurable b) {
        if (!isSameType(a, b)) {
            throw new QuantityMeasurementException(
                    "Different measurement types");
        }
    }
}