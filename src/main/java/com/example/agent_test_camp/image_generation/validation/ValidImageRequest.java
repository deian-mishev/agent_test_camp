package com.example.agent_test_camp.image_generation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniformSizeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImageRequest {
  String message() default "When uniformSize is true, width and height must be equal";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
