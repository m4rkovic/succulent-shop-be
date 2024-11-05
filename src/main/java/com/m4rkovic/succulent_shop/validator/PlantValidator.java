package com.m4rkovic.succulent_shop.validator;

import com.m4rkovic.succulent_shop.dto.PlantDTO;
import com.m4rkovic.succulent_shop.enumerator.Color;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class PlantValidator {

    public List<String> validate(PlantDTO plantDto) {
        List<String> violations = new ArrayList<>();

        validateBasicFields(plantDto, violations);
        validateEnums(plantDto, violations);

        return violations;
    }

    private void validateBasicFields(PlantDTO plantDto, List<String> violations) {
        if (StringUtils.isBlank(plantDto.getName())) {
            violations.add("Plant name cannot be empty!");
        }
        if ((plantDto.getCategoryId().equals(null))) {
            violations.add("Plant category cannot be empty!");
        }
    }

    public void validateAndThrow(PlantDTO plantDto) {
        List<String> violations = validate(plantDto);
        if (!violations.isEmpty()) {
            throw new RuntimeException(String.join(", ", violations));
        }
    }

    public void validateEnums(PlantDTO plantDto, List<String> violations) {

        if (StringUtils.isNotBlank(plantDto.getPrimaryColor())) {
            try {
                Color.valueOf(plantDto.getPrimaryColor().toUpperCase());
            } catch (IllegalArgumentException e) {
                violations.add("Invalid primary color!");
            }
        }

        if (StringUtils.isNotBlank(plantDto.getSecondaryColor())) {
            try {
                Color.valueOf(plantDto.getSecondaryColor().toUpperCase());
            } catch (IllegalArgumentException e) {
                violations.add("Invalid secondary color!");
            }
        }

        if (StringUtils.isNotBlank(plantDto.getBloomColor())) {
            try {
                Color.valueOf(plantDto.getBloomColor().toUpperCase());
            } catch (IllegalArgumentException e) {
                violations.add("Invalid bloom color!");
            }
        }
    }
}
