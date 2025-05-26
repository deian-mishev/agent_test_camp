package com.example.agent_test_camp.image_generation.validation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
    String errors =
        ex.getBindingResult().getFieldErrors().stream()
            .sorted(
                (e1, e2) -> {
                  boolean e1Required = e1.getDefaultMessage().toLowerCase().contains("required");
                  boolean e2Required = e2.getDefaultMessage().toLowerCase().contains("required");
                  return Boolean.compare(!e1Required, !e2Required);
                })
            .map(error -> "- " + error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining("\n"));

    String body = "Validation failed:\n" + errors;
    return ResponseEntity.badRequest().body(body);
  }
}
