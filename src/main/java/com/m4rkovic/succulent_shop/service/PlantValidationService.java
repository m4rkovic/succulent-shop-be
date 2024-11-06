package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.dto.PlantDTO;
import com.m4rkovic.succulent_shop.exceptions.ValidationException;
import com.m4rkovic.succulent_shop.validator.PlantValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlantValidationService {

    private  final PlantValidator plantValidator;

    public void validatePlantDTO(PlantDTO plantDTO) {
        if (plantDTO == null) {
            throw new ValidationException("Plant data cannot be null");
        }

        List<String> violations = plantValidator.validate(plantDTO);
        if (!violations.isEmpty()) {
            throw new ValidationException("Plant validation failed ", violations);
        }
    }
}
