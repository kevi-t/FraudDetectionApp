package fraud.detection.app.services;

import fraud.detection.app.responses.UniversalResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice

public class ControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
        public UniversalResponse handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
            Map<String, String> errors = new HashMap<>();
            ex.getBindingResult().getFieldErrors().forEach(error -> {

            });
            return UniversalResponse.builder()
                    .message("MethodArgumentNotValidException")
                    .data(null)
                    .status("1")
                    .build();
        }

        @ExceptionHandler(ConstraintViolationException.class)
        public UniversalResponse handleConstraintViolationException(ConstraintViolationException ex) {
            Map<String, String> errors = new HashMap<>();
            ex.getConstraintViolations().forEach(violation -> {
                String fieldName = violation.getPropertyPath().toString();
                String errorMessage = violation.getMessage();
            });
            return  UniversalResponse.builder()
                    .message("ConstraintViolationException")
                    .data(null)
                    .status("1")
                    .build();
        }
    }


