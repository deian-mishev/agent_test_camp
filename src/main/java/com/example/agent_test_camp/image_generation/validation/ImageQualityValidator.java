package com.example.agent_test_camp.image_generation.validation;

import com.example.agent_test_camp.image_generation.configuration.ImageProperties;
import jakarta.servlet.annotation.WebFilter;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class ImageQualityValidator implements ConstraintValidator<ValidImageQuality, String> {

  private final Set<String> allowedQualities;

  @Autowired
  public ImageQualityValidator(ImageProperties imageProperties) {
    this.allowedQualities = imageProperties.getAllowedQualities();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) return true;

    if (!allowedQualities.contains(value)) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate("Must be one of " + allowedQualities)
          .addConstraintViolation();
      return false;
    }

    return true;
  }
}
