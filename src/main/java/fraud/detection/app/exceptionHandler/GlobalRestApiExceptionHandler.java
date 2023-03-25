package fraud.detection.app.exceptionHandler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@ControllerAdvice
public class GlobalRestApiExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
                List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
                //ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
                int  code = ex.getBody().getStatus();
                return new ResponseEntity<>(getErrorsMap(errors, Collections.singletonList(String.valueOf(code))), new HttpHeaders(),ex.getBody().getStatus());
        }

        private Map<String, List<String>> getErrorsMap(List<String> errors,List<String> code) {
                Map<String, List<String>> errorResponse = new HashMap<>();
                errorResponse.put("errors", errors);
                errorResponse.put("code", code);
                return errorResponse;
        }
//        @ExceptionHandler(UsernameNotFoundException.class)
//        public ResponseEntity<Map<String, List<String>>> handleNotFoundException(UsernameNotFoundException ex) {
//                List<String> errors = Collections.singletonList(ex.getMessage());
//                return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.NOT_FOUND);
//        }
//
//        @ExceptionHandler(Exception.class)
//        public final ResponseEntity<Map<String, List<String>>> handleGeneralExceptions(Exception ex) {
//                List<String> errors = Collections.singletonList(ex.getMessage());
//                return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//        @ExceptionHandler(RuntimeException.class)
//        public final ResponseEntity<Map<String, List<String>>> handleRuntimeExceptions(RuntimeException ex) {
//                List<String> errors = Collections.singletonList(ex.getMessage());
//                return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
}