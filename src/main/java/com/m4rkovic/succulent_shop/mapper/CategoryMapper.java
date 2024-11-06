package com.m4rkovic.succulent_shop.mapper;

import com.m4rkovic.succulent_shop.dto.CategoryDTO;
import com.m4rkovic.succulent_shop.entity.Category;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CategoryMapper {

    public Category toEntity(CategoryDTO dto) {
        if (dto == null) {
            return null;
        }

        return Category.builder()
                .id(dto.getId())
                .categoryName(dto.getCategoryName())
                .categoryDesc(dto.getCategoryDesc())
                .build();
    }

    public CategoryDTO toDTO(Category entity) {
        if (entity == null) {
            return null;
        }

        return CategoryDTO.builder()
                .id(entity.getId())
                .categoryName(entity.getCategoryName())
                .categoryDesc(entity.getCategoryDesc())
                .build();
    }

    public void updateEntityFromDTO(Category entity, CategoryDTO dto) {
        if (entity == null || dto == null) {
            return;
        }

        if (StringUtils.isNotBlank(dto.getCategoryName())) {
            entity.setCategoryName(dto.getCategoryName());
        }
        if (StringUtils.isNotBlank(dto.getCategoryDesc())) {
            entity.setCategoryDesc(dto.getCategoryDesc());
        }
    }

    public List<CategoryDTO> toDTOList(List<Category> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}