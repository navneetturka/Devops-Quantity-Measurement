package com.apps.quantitymeasurement.service;

import com.apps.quantitymeasurement.model.QuantityDTO;
import com.apps.quantitymeasurement.model.QuantityMeasurementDTO;
import com.apps.quantitymeasurement.model.QuantityMeasurementEntity;
import com.apps.quantitymeasurement.exception.QuantityMeasurementException;
import com.apps.quantitymeasurement.repository.QuantityMeasurementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuantityMeasurementServiceTest {

    @Mock
    private QuantityMeasurementRepository repository;

    @InjectMocks
    private QuantityMeasurementServiceImpl service;

    @Test
    public void testCompare_FeetAndInches_Equal_ReturnsTrue() {
        QuantityDTO feet = new QuantityDTO(1.0, "FEET", "LengthUnit");
        QuantityDTO inches = new QuantityDTO(12.0, "INCHES", "LengthUnit");

        QuantityMeasurementDTO result = service.compare(feet, inches);

        assertThat(result.getResultString()).isEqualTo("true");
        verify(repository).save(any(QuantityMeasurementEntity.class));
    }

    @Test
    public void testCompare_DifferentTypes_ReturnsFalse() {
        QuantityDTO length = new QuantityDTO(1.0, "FEET", "LengthUnit");
        QuantityDTO weight = new QuantityDTO(1.0, "KILOGRAM", "WeightUnit");

        QuantityMeasurementDTO result = service.compare(length, weight);

        assertThat(result.getResultString()).isEqualTo("false");
    }

    @Test
    public void testConvert_GallonLitres() {
        QuantityDTO g = new QuantityDTO(1.0, "GALLON", "VolumeUnit");
        QuantityDTO l = new QuantityDTO(0.0, "LITRE", "VolumeUnit");

        QuantityMeasurementDTO result = service.convert(g, l);

        assertThat(result.getResultValue())
                .isCloseTo(3.78541, within(0.0001));
    }

    @Test
    public void testAdd_FeetAndInches() {
        QuantityDTO feet = new QuantityDTO(1.0, "FEET", "LengthUnit");
        QuantityDTO inches = new QuantityDTO(12.0, "INCHES", "LengthUnit");

        QuantityMeasurementDTO result = service.add(feet, inches);

        assertThat(result.getResultValue()).isEqualTo(2.0);
    }

    @Test
    public void testDivide_ByZero_ThrowsException() {
        QuantityDTO yard = new QuantityDTO(1.0, "YARDS", "LengthUnit");
        QuantityDTO zero = new QuantityDTO(0.0, "FEET", "LengthUnit");

        assertThatThrownBy(() -> service.divide(yard, zero))
                .isInstanceOf(QuantityMeasurementException.class)
                .hasMessageContaining("Divide by zero");
    }

    @Test
    public void testDivide_Temperature_ThrowsUnsupported() {
        QuantityDTO c = new QuantityDTO(100.0, "CELSIUS", "TemperatureUnit");
        QuantityDTO f = new QuantityDTO(50.0, "FAHRENHEIT", "TemperatureUnit");

        assertThatThrownBy(() -> service.divide(c, f))
                .isInstanceOf(QuantityMeasurementException.class);
    }

    @Test
    public void testGetOperationHistory() {

        when(repository.findByOperation("compare"))
                .thenReturn(Collections.emptyList());

        List<QuantityMeasurementDTO> result =
                service.getOperationHistory("compare");

        assertThat(result).isEmpty();
        verify(repository).findByOperation("compare");
    }
}