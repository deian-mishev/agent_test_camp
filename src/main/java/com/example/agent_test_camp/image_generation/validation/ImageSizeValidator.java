package com.example.agent_test_camp.image_generation.validation;

import com.example.agent_test_camp.image_generation.configuration.ImageProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class ImageSizeValidator implements ConstraintValidator<ValidImageSize, Integer> {

  private final Set<Integer> allowedSizes;

  @Autowired
  public ImageSizeValidator(ImageProperties imageProperties) {
    this.allowedSizes = imageProperties.getAllowedSizes();
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext context) {
    if (value == null) return true;

    if (!allowedSizes.contains(value)) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate("Must be one of " + allowedSizes.toString())
          .addConstraintViolation();
      return false;
    }

    return true;
  }
}
