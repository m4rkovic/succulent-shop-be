package com.m4rkovic.succulent_shop.mapper;


import com.m4rkovic.succulent_shop.dto.PlantDTO;
import com.m4rkovic.succulent_shop.entity.Category;
import com.m4rkovic.succulent_shop.entity.Plant;
import com.m4rkovic.succulent_shop.enumerator.Color;
import com.m4rkovic.succulent_shop.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PlantMapper {

    private final CategoryService categoryService;

    public PlantMapper(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public Plant toEntity(PlantDTO dto) {
        if (dto == null) {
            return null;
        }

        Category category = dto.getCategoryId() != null ?
                categoryService.findById(dto.getCategoryId()) : null;

        return Plant.builder()
                .id(dto.getId())
                .name(dto.getName())
                .plantPhoto(dto.getPlantPhoto())
                .primaryColor(StringUtils.isNotBlank(dto.getPrimaryColor()) ?
                        Color.valueOf(dto.getPrimaryColor().toUpperCase()) : null)
                .secondaryColor(StringUtils.isNotBlank(dto.getSecondaryColor()) ?
                        Color.valueOf(dto.getSecondaryColor().toUpperCase()) : null)
                .bloomColor(StringUtils.isNotBlank(dto.getBloomColor()) ?
                        Color.valueOf(dto.getBloomColor().toUpperCase()) : null)
                .category(category)
                .build();
    }

    public PlantDTO toDTO(Plant entity) {
        if (entity == null) {
            return null;
        }

        return PlantDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .plantPhoto(entity.getPlantPhoto())
                .primaryColor(entity.getPrimaryColor() != null ?
                        entity.getPrimaryColor().name() : null)
                .secondaryColor(entity.getSecondaryColor() != null ?
                        entity.getSecondaryColor().name() : null)
                .bloomColor(entity.getBloomColor() != null ?
                        entity.getBloomColor().name() : null)
                .categoryId(entity.getCategory() != null ?
                        entity.getCategory().getId() : null)
                .build();
    }

    public void updateEntityFromDTO(Plant entity, PlantDTO dto) {
        if (entity == null || dto == null) {
            return;
        }

        if (StringUtils.isNotBlank(dto.getName())) {
            entity.setName(dto.getName());
        }
        if (StringUtils.isNotBlank(dto.getPrimaryColor())) {
            entity.setPrimaryColor(Color.valueOf(dto.getPrimaryColor().toUpperCase()));
        }
        if (StringUtils.isNotBlank(dto.getSecondaryColor())) {
            entity.setSecondaryColor(Color.valueOf(dto.getSecondaryColor().toUpperCase()));
        }
        if (StringUtils.isNotBlank(dto.getBloomColor())) {
            entity.setBloomColor(Color.valueOf(dto.getBloomColor().toUpperCase()));
        }
        if (dto.getCategoryId() != null) {
            entity.setCategory(categoryService.findById(dto.getCategoryId()));
        }
        if (dto.getPlantPhoto() != null) {
            entity.setPlantPhoto(dto.getPlantPhoto());
        }
    }

    public List<PlantDTO> toDTOList(List<Plant> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}