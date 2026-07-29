package com.apps.quantitymeasurement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

class ErrorResponse {
    public LocalDateTime timestamp;
    public int           status;
    public String        error;
    public String        message;
    public String        path;
}

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            Logger.getLogger(GlobalExceptionHandler.class.getName());

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        List<String> messages = errors.stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());

        ErrorResponse response = new ErrorResponse();
        response.timestamp     = LocalDateTime.now();
        response.status        = HttpStatus.BAD_REQUEST.value();
        response.error         = "Quantity Measurement Error";
        response.message       = String.join("; ", messages);
        response.path          = "quantityInputDTO";

        logger.warning("Validation error: " + response.message);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(QuantityMeasurementException.class)
    public ResponseEntity<ErrorResponse> handleQuantityException(
            QuantityMeasurementException ex,
            WebRequest request) {

        ErrorResponse response = new ErrorResponse();
        response.timestamp     = LocalDateTime.now();
        response.status        = HttpStatus.BAD_REQUEST.value();
        response.error         = "Quantity Measurement Error";
        response.message       = ex.getMessage();
        response.path          = request.getDescription(false)
                .replace("uri=", "");

        logger.warning("QuantityMeasurementException: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {

        ErrorResponse response = new ErrorResponse();
        response.timestamp     = LocalDateTime.now();
        response.status        = HttpStatus.INTERNAL_SERVER_ERROR.value();
        response.error         = "Internal Server Error";
        response.message       = ex.getMessage();
        response.path          = request.getDescription(false)
                .replace("uri=", "");

        logger.severe("Unhandled exception: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}