package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.entity.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {

    public List<Category> findAll();

    public Category findById(Long id);

    public Category save(Category category);

    public void deleteById(Long catId);
}
