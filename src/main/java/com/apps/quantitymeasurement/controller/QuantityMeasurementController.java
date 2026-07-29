package com.apps.quantitymeasurement.controller;

import com.apps.quantitymeasurement.model.QuantityInputDTO;
import com.apps.quantitymeasurement.model.QuantityMeasurementDTO;
import com.apps.quantitymeasurement.service.IQuantityMeasurementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/quantities")
@Tag(
        name        = "Quantity Measurements",
        description = "REST API for quantity measurement operations"
)
public class QuantityMeasurementController {

    private static final Logger logger =
            Logger.getLogger(QuantityMeasurementController.class.getName());

    @Autowired
    private IQuantityMeasurementService service;

    private static final String EX_FEET_INCH =
            """
            {"thisQuantityDTO":{"value":1.0,"unit":"FEET","measurementType":"LengthUnit"},
             "thatQuantityDTO":{"value":12.0,"unit":"INCHES","measurementType":"LengthUnit"}}
            """;

    private static final String EX_YARD_FEET =
            """
            {"thisQuantityDTO":{"value":1.0,"unit":"YARDS","measurementType":"LengthUnit"},
             "thatQuantityDTO":{"value":3.0,"unit":"FEET","measurementType":"LengthUnit"}}
            """;

    private static final String EX_GALLON_LITRE =
            """
            {"thisQuantityDTO":{"value":1.0,"unit":"GALLON","measurementType":"VolumeUnit"},
             "thatQuantityDTO":{"value":3.785,"unit":"LITRE","measurementType":"VolumeUnit"}}
            """;

    private static final String EX_TEMP =
            """
            {"thisQuantityDTO":{"value":212.0,"unit":"FAHRENHEIT","measurementType":"TemperatureUnit"},
             "thatQuantityDTO":{"value":100.0,"unit":"CELSIUS","measurementType":"TemperatureUnit"}}
            """;

    private static final String EX_WITH_TARGET =
            """
            {"thisQuantityDTO":  {"value":1.0, "unit":"FEET",   "measurementType":"LengthUnit"},
             "thatQuantityDTO":  {"value":12.0,"unit":"INCHES", "measurementType":"LengthUnit"},
             "targetQuantityDTO":{"value":0.0, "unit":"INCHES", "measurementType":"LengthUnit"}}
            """;

    @PostMapping("/compare")
    @Operation(
            summary = "Compare two quantities",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = {
                            @ExampleObject(name = "Feet = 12 Inches", value = EX_FEET_INCH),
                            @ExampleObject(name = "Yard = 3 Feet",    value = EX_YARD_FEET),
                            @ExampleObject(name = "Gallon = Litres",  value = EX_GALLON_LITRE),
                            @ExampleObject(name = "212°F = 100°C",    value = EX_TEMP)
                    })
            )
    )
    public ResponseEntity<QuantityMeasurementDTO> performComparison(
            @Valid @RequestBody QuantityInputDTO quantityInputDTO) {
        logger.info("POST /compare");
        return ResponseEntity.ok(service.compare(
                quantityInputDTO.getThisQuantityDTO(),
                quantityInputDTO.getThatQuantityDTO()));
    }

    @PostMapping("/convert")
    @Operation(
            summary = "Convert quantity to target unit",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = {
                            @ExampleObject(name = "Feet -> Inches",   value = EX_FEET_INCH),
                            @ExampleObject(name = "Yard -> Feet",     value = EX_YARD_FEET),
                            @ExampleObject(name = "Gallon -> Litres", value = EX_GALLON_LITRE),
                            @ExampleObject(name = "212°F -> 100°C",  value = EX_TEMP)
                    })
            )
    )
    public ResponseEntity<QuantityMeasurementDTO> performConversion(
            @Valid @RequestBody QuantityInputDTO quantityInputDTO) {
        logger.info("POST /convert");
        return ResponseEntity.ok(service.convert(
                quantityInputDTO.getThisQuantityDTO(),
                quantityInputDTO.getThatQuantityDTO()));
    }

    @PostMapping("/add")
    @Operation(
            summary = "Add two quantities",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = {
                            @ExampleObject(name = "Feet + Inches",   value = EX_FEET_INCH),
                            @ExampleObject(name = "Yard + Feet",     value = EX_YARD_FEET),
                            @ExampleObject(name = "Gallon + Litres", value = EX_GALLON_LITRE)
                    })
            )
    )
    public ResponseEntity<QuantityMeasurementDTO> performAddition(
            @Valid @RequestBody QuantityInputDTO quantityInputDTO) {
        logger.info("POST /add");
        return ResponseEntity.ok(service.add(
                quantityInputDTO.getThisQuantityDTO(),
                quantityInputDTO.getThatQuantityDTO()));
    }

    @PostMapping("/add-with-target-unit")
    @Operation(
            summary = "Add two quantities with a target unit",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = {
                            @ExampleObject(name = "Feet + Inches with Target", value = EX_WITH_TARGET)
                    })
            )
    )
    public ResponseEntity<QuantityMeasurementDTO> performAdditionWithTargetUnit(
            @Valid @RequestBody QuantityInputDTO quantityInputDTO) {
        logger.info("POST /add-with-target-unit");
        return ResponseEntity.ok(service.add(
                quantityInputDTO.getThisQuantityDTO(),
                quantityInputDTO.getThatQuantityDTO(),
                quantityInputDTO.getTargetQuantityDTO() != null
                        ? quantityInputDTO.getTargetQuantityDTO()
                        : quantityInputDTO.getThisQuantityDTO()));
    }

    @PostMapping("/subtract")
    @Operation(
            summary = "Subtract two quantities",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = {
                            @ExampleObject(name = "Feet - Inches",   value = EX_FEET_INCH),
                            @ExampleObject(name = "Yard - Feet",     value = EX_YARD_FEET),
                            @ExampleObject(name = "Gallon - Litres", value = EX_GALLON_LITRE)
                    })
            )
    )
    public ResponseEntity<QuantityMeasurementDTO> performSubtraction(
            @Valid @RequestBody QuantityInputDTO quantityInputDTO) {
        logger.info("POST /subtract");
        return ResponseEntity.ok(service.subtract(
                quantityInputDTO.getThisQuantityDTO(),
                quantityInputDTO.getThatQuantityDTO()));
    }

    @PostMapping("/subtract-with-target-unit")
    @Operation(
            summary = "Subtract two quantities with target unit",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = {
                            @ExampleObject(name = "Feet - Inches with Target", value = EX_WITH_TARGET)
                    })
            )
    )
    public ResponseEntity<QuantityMeasurementDTO> performSubtractionWithTargetUnit(
            @Valid @RequestBody QuantityInputDTO quantityInputDTO) {
        logger.info("POST /subtract-with-target-unit");
        return ResponseEntity.ok(service.subtract(
                quantityInputDTO.getThisQuantityDTO(),
                quantityInputDTO.getThatQuantityDTO(),
                quantityInputDTO.getTargetQuantityDTO() != null
                        ? quantityInputDTO.getTargetQuantityDTO()
                        : quantityInputDTO.getThisQuantityDTO()));
    }

    @PostMapping("/divide")
    @Operation(
            summary = "Divide two quantities",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = {
                            @ExampleObject(name = "Feet / Inches",   value = EX_FEET_INCH),
                            @ExampleObject(name = "Yard / Feet",     value = EX_YARD_FEET),
                            @ExampleObject(name = "Gallon / Litres", value = EX_GALLON_LITRE)
                    })
            )
    )
    public ResponseEntity<QuantityMeasurementDTO> performDivision(
            @Valid @RequestBody QuantityInputDTO quantityInputDTO) {
        logger.info("POST /divide");
        return ResponseEntity.ok(service.divide(
                quantityInputDTO.getThisQuantityDTO(),
                quantityInputDTO.getThatQuantityDTO()));
    }

    @GetMapping("/history/operation/{operation}")
    @Operation(
            summary     = "Get operation history",
            description = "Valid operations: ADD, SUBTRACT, DIVIDE, CONVERT, COMPARE"
    )
    public ResponseEntity<List<QuantityMeasurementDTO>> getOperationHistory(
            @PathVariable String operation) {
        logger.info("GET /history/operation/" + operation);
        return ResponseEntity.ok(service.getOperationHistory(operation));
    }

    @GetMapping("/history/type/{type}")
    @Operation(
            summary     = "Get operation history by type",
            description = "Valid types: LengthUnit, VolumeUnit, WeightUnit, TemperatureUnit"
    )
    public ResponseEntity<List<QuantityMeasurementDTO>> getOperationHistoryByType(
            @PathVariable String type) {
        logger.info("GET /history/type/" + type);
        return ResponseEntity.ok(service.getMeasurementsByType(type));
    }

    @GetMapping("/count/{operation}")
    @Operation(
            summary     = "Get operation count",
            description = "Valid operations: ADD, SUBTRACT, DIVIDE, CONVERT, COMPARE"
    )
    public ResponseEntity<Long> getOperationCount(
            @PathVariable String operation) {
        logger.info("GET /count/" + operation);
        return ResponseEntity.ok(service.getOperationCount(operation));
    }

    @GetMapping("/history/errored")
    @Operation(summary = "Get errored operations history")
    public ResponseEntity<List<QuantityMeasurementDTO>> getErroredOperations() {
        logger.info("GET /history/errored");
        return ResponseEntity.ok(service.getErrorHistory());
    }

    @DeleteMapping("/history/{id}")
    @Operation(summary = "Delete a single history record")
    public ResponseEntity<Void> deleteHistoryEntry(@PathVariable Long id) {
        logger.info("DELETE /history/" + id);
        service.deleteHistoryEntry(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/history")
    @Operation(summary = "Clear all history records")
    public ResponseEntity<Void> clearAllHistory() {
        logger.info("DELETE /history (clear all)");
        service.clearAllHistory();
        return ResponseEntity.noContent().build();
    }
}