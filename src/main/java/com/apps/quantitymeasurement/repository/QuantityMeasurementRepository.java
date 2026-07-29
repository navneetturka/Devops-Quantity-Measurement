package com.apps.quantitymeasurement.repository;

import com.apps.quantitymeasurement.model.QuantityMeasurementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuantityMeasurementRepository
        extends JpaRepository<QuantityMeasurementEntity, Long> {

    List<QuantityMeasurementEntity> findByOperation(String operation);

    List<QuantityMeasurementEntity> findByThisMeasurementType(
            String measurementType);

    List<QuantityMeasurementEntity> findByCreatedAtAfter(LocalDateTime date);

    @Query("""
            SELECT e FROM QuantityMeasurementEntity e
            WHERE e.operation = :operation
              AND e.error = false
            """)
    List<QuantityMeasurementEntity> findSuccessfulOperations(
            @Param("operation") String operation);

    long countByOperationAndErrorFalse(String operation);

    List<QuantityMeasurementEntity> findByErrorTrue();
}