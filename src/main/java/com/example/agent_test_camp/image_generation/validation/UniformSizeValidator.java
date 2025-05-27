package com.example.agent_test_camp.image_generation.validation;
import com.example.agent_test_camp.image_generation.configuration.ImageProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.example.agent_test_camp.image_generation.dto.ImageRequest;
import org.springframework.beans.factory.annotation.Autowired;

public class UniformSizeValidator implements ConstraintValidator<ValidImageRequest, ImageRequest> {

    private final ImageProperties imageProperties;

    @Autowired
    public UniformSizeValidator(ImageProperties imageProperties) {
        this.imageProperties = imageProperties;
    }

    @Override
    public boolean isValid(ImageRequest request, ConstraintValidatorContext context) {
        if (request == null) return true;

        Boolean uniform = imageProperties.getUniformSize();
        Integer width = request.getWidth();
        Integer height = request.getHeight();

        if (Boolean.TRUE.equals(uniform) && width != null && height != null && !width.equals(height)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Width and height must be equal when uniformSize is true")
                    .addPropertyNode("height")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}