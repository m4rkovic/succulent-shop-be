package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.dto.CategoryDTO;
import com.m4rkovic.succulent_shop.entity.Category;
import com.m4rkovic.succulent_shop.exceptions.CreationException;
import com.m4rkovic.succulent_shop.exceptions.DeleteException;
import com.m4rkovic.succulent_shop.exceptions.ResourceNotFoundException;
import com.m4rkovic.succulent_shop.exceptions.UpdateException;
import com.m4rkovic.succulent_shop.mapper.CategoryMapper;
import com.m4rkovic.succulent_shop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryValidationService validationService;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        log.debug("Retrieving all categories!");
        return categoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Category findById(Long id) {
        log.debug("Retrieving category with id: {}", id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Category not found with id: %d", id)));
    }

    @Override
    @Transactional
    public Category save(CategoryDTO categoryDTO) {
        log.debug("Creating new category");
        validationService.validateCategoryDTO(categoryDTO);

        try {
            Category category = categoryMapper.toEntity(categoryDTO);
            return categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            throw new CreationException("Failed to create category due to data integrity violation", e);
        }
    }

    @Override
    @Transactional
    public Category update(Long id, CategoryDTO categoryDTO) {
        log.debug("Updating category with id: {}", id);
        Category existingCategory = findById(id);
        validationService.validateCategoryDTO(categoryDTO);

        try {
            categoryMapper.updateEntityFromDTO(existingCategory, categoryDTO);
            return categoryRepository.save(existingCategory);
        } catch (DataIntegrityViolationException e) {
            throw new UpdateException(String.format("Failed to update category with id: %d", id), e);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long categoryId) {
        log.debug("Deleting category with id: {}", categoryId);
        findById(categoryId);

        try {
            categoryRepository.deleteById(categoryId);
        } catch (DataIntegrityViolationException e) {
            throw new DeleteException(
                    String.format("Cannot delete category with id: %d due to existing references", categoryId), e);
        }
    }
}