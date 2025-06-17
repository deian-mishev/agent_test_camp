package com.example.agent_test_camp.image_generation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ImageQualityValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImageQuality {
  String message() default "Invalid quality";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
