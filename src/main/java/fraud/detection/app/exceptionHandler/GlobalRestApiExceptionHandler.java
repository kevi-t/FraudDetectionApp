package fraud.detection.app.exceptionHandler;

import fraud.detection.app.responses.UniversalResponse;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@ControllerAdvice
public class GlobalRestApiExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public UniversalResponse handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
                List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
                String errorMessage = String.join(",", errors);
                return UniversalResponse.builder()
                        .message(errorMessage)
                        .status("1")
                        .build();
        }

//    @ExceptionHandler(ConstraintViolationException.class)
//    public UniversalResponse handleConstraintViolationException(ConstraintViolationException ex) {
//        Map<String, String> errors = new HashMap<>();
//        ex.getConstraintViolations().forEach(violation -> {
//            String fieldName = violation.getPropertyPath().toString();
//            String errorMessage = violation.getMessage();
//            errors.put(fieldName, errorMessage);
//        });
//        String errorMessage = String.join(",", errors.values());
//        return UniversalResponse.builder()
//                .message(errorMessage)
//                .data(null)
//                .status("1")
//                .build();
//    }
}