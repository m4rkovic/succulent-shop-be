package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.dto.CategoryDTO;
import com.m4rkovic.succulent_shop.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {

    public List<Category> findAll();

    public Page<Category> findAllPaginated(Pageable pageable);
    public Category findById(Long id);
    public Category save(CategoryDTO categoryDTO);

    public Category update(Long id, CategoryDTO categoryDTO);
    public void deleteById(Long catId);
}
