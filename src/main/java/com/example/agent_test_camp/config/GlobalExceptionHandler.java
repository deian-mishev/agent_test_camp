package com.example.agent_test_camp.config;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@ControllerAdvice
@Profile({"image_generation", "loan_chat", "recipes_chat", "translator", "trace_decoder"})
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

  @ExceptionHandler(ExecutionException.class)
  public ResponseEntity<String> handleExecutionException(ExecutionException ex) {
    String errors = ex.getCause().getLocalizedMessage();
    return getExceptionFormat(errors);
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<String> handleNotFound(NoSuchElementException ex) {
    return getExceptionFormat(ex.getMessage());
  }

  private ResponseEntity<String> getExceptionFormat(String error) {
    return ResponseEntity.badRequest().body("Validation failed: " + error);
  }
}
