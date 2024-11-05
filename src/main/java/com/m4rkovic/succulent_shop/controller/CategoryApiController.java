package com.m4rkovic.succulent_shop.controller;

import com.m4rkovic.succulent_shop.entity.Category;
import com.m4rkovic.succulent_shop.repository.CategoryRepository;
import com.m4rkovic.succulent_shop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@CrossOrigin
public class CategoryApiController {

    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    @GetMapping("/{id}")
    public Category getCategory(@PathVariable Long id) {
        return categoryService.findById(id);
    }

    @GetMapping
    public List<Category> getCategories() {
        return categoryService.findAll();
    }

    @PostMapping("/createCategory")
    public ResponseEntity createCategory(@RequestBody Category category) throws URISyntaxException {
        Category savedCategory = categoryService.save(category);
        return ResponseEntity.created(new URI("/categories/" + savedCategory.getId())).body(savedCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateProduct(@PathVariable Long id, @RequestBody Category category) {
        Category currenctCategory = categoryService.findById(id);
        currenctCategory.setCategoryName(category.getCategoryName());
        currenctCategory.setCategoryDesc(category.getCategoryDesc());
       // currenctCategory = categoryRepository.save(category);

        return ResponseEntity.ok(currenctCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
