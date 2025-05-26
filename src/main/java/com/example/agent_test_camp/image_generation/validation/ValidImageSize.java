package com.example.agent_test_camp.image_generation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ImageSizeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImageSize {
  String message() default "Invalid size";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
