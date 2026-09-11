package se.meepo.dinso.api;

import java.util.Map;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(IllegalArgumentException.class) ResponseEntity<Map<String, String>> invalidInput(IllegalArgumentException exception) { return response(HttpStatus.BAD_REQUEST, exception.getMessage()); }
  @ExceptionHandler(SecurityException.class) ResponseEntity<Map<String, String>> forbidden(SecurityException exception) { return response(HttpStatus.FORBIDDEN, exception.getMessage()); }
  private ResponseEntity<Map<String, String>> response(HttpStatus status, String message) { return ResponseEntity.status(status).body(Map.of("message", message)); }
}
