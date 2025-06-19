package com.example.agent_test_camp.image_generation.validation;

import com.example.agent_test_camp.image_generation.configuration.ImageProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class ImageStylesValidator implements ConstraintValidator<ValidImageStyle, String> {

  private final Set<String> allowedStyles;

  @Autowired
  public ImageStylesValidator(ImageProperties imageProperties) {
    this.allowedStyles = imageProperties.getAllowedStyles();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) return true;

    if (!allowedStyles.contains(value)) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate("Must be one of " + allowedStyles)
          .addConstraintViolation();
      return false;
    }

    return true;
  }
}
