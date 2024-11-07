package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.dto.PlantDTO;
import com.m4rkovic.succulent_shop.entity.Category;
import com.m4rkovic.succulent_shop.entity.Plant;
import com.m4rkovic.succulent_shop.enumerator.Color;
import com.m4rkovic.succulent_shop.exceptions.CreationException;
import com.m4rkovic.succulent_shop.exceptions.ResourceNotFoundException;
import com.m4rkovic.succulent_shop.exceptions.ValidationException;
import com.m4rkovic.succulent_shop.mapper.PlantMapper;
import com.m4rkovic.succulent_shop.repository.PlantRepository;
import com.m4rkovic.succulent_shop.validator.PlantValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlantServiceImpl implements PlantService {

    private final PlantRepository plantRepository;
    private final FileStorageService fileStorageService;

    private final PlantValidationService validationService;
    private final PlantMapper plantMapper;

    // FIND ALL
    @Override
    @Transactional(readOnly = true)
    public List<Plant> findAll() {
        log.debug("Retrieving all plants!");
        return plantRepository.findAll();
    }

    // FIND BY ID
    @Override
    @Transactional(readOnly = true)
    public Plant findById(Long id) {
        log.debug("Retrieving plant with id: {}", id);
        return plantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Plant not found with id: %d", id)));
    }

    // SAVE
    @Override
    @Transactional
    public Plant save(String name, Color primaryColor, Color secondaryColor, Color bloomColor,
                      MultipartFile photoFile, Category category) {
        PlantDTO plantDto = PlantDTO.builder()
                .name(name)
                .primaryColor(primaryColor != null ? primaryColor.name() : null)
                .secondaryColor(secondaryColor != null ? secondaryColor.name() : null)
                .bloomColor(bloomColor != null ? bloomColor.name() : null)
                .photoFile(photoFile)
                .categoryId(category != null ? category.getId() : null)
                .build();

        validationService.validatePlantDTO(plantDto);

        try {
            Plant plant = plantMapper.toEntity(plantDto);
            plant.setCategory(category);

            if (photoFile != null && !photoFile.isEmpty()) {
                String fileName = fileStorageService.storeFile(photoFile);
                plant.setPlantPhoto(fileName);
            }

            return plantRepository.save(plant);
        } catch (DataIntegrityViolationException e) {
            throw new CreationException("Failed to create plant due to data integrity violation", e);
        }
    }


    // UPDATE
    @Override
    @Transactional
    public Plant update(Long id, PlantDTO plantDto) {
        Plant existingPlant = findById(id);

        existingPlant.setName(plantDto.getName());
        existingPlant.setPrimaryColor(Color.valueOf(plantDto.getPrimaryColor()));
        existingPlant.setSecondaryColor(Color.valueOf(plantDto.getSecondaryColor()));
        existingPlant.setBloomColor(Color.valueOf(plantDto.getBloomColor()));
        if (plantDto.getPhotoFile() != null && !plantDto.getPhotoFile().isEmpty()) {
            if (existingPlant.getPlantPhoto() != null) {
                fileStorageService.deleteFile(existingPlant.getPlantPhoto());
            }

            String fileName = fileStorageService.storeFile(plantDto.getPhotoFile());
            existingPlant.setPlantPhoto(fileName);
        }

        return plantRepository.save(existingPlant);
    }

    // DELETE BY ID
    @Override
    @Transactional
    public void deleteById(Long plantId) {
        Plant plant = findById(plantId);

        if (plant.getPlantPhoto() != null) {
            fileStorageService.deleteFile(plant.getPlantPhoto());
        }

        plantRepository.deleteById(plantId);
    }

}
