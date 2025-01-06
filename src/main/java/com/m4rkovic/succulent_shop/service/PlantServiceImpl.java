package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.dto.PlantDTO;
import com.m4rkovic.succulent_shop.entity.Category;
import com.m4rkovic.succulent_shop.entity.Plant;
import com.m4rkovic.succulent_shop.enumerator.Color;
import com.m4rkovic.succulent_shop.exceptions.CreationException;
import com.m4rkovic.succulent_shop.exceptions.ResourceNotFoundException;
import com.m4rkovic.succulent_shop.mapper.PlantMapper;
import com.m4rkovic.succulent_shop.repository.PlantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    @Transactional(readOnly = true)
    public Page<Plant> findAllPaginated(Pageable pageable) {
        log.debug("Retrieving all plants with pagination! Page: {}, Size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return plantRepository.findAll(pageable);
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
    public Plant save(String name, String careInstructions, Color primaryColor,
                      Color secondaryColor, Color bloomColor, Category category) {
        PlantDTO plantDto = PlantDTO.builder()
                .name(name)
                .careInstructions(careInstructions)
                .primaryColor(primaryColor != null ? primaryColor.name() : null)
                .secondaryColor(secondaryColor != null ? secondaryColor.name() : null)
                .bloomColor(bloomColor != null ? bloomColor.name() : null)
                .categoryId(category != null ? category.getId() : null)
                .build();

        validationService.validatePlantDTO(plantDto);

        try {
            Plant plant = plantMapper.toEntity(plantDto);
            plant.setCategory(category);
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
        if (plantDto.getCareInstructions() != null) {
            existingPlant.setCareInstructions(plantDto.getCareInstructions());
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
