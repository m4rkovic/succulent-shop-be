package com.m4rkovic.succulent_shop.controller;

import com.m4rkovic.succulent_shop.dto.CategoryDTO;
import com.m4rkovic.succulent_shop.entity.Category;
import com.m4rkovic.succulent_shop.exceptions.InvalidDataException;
import com.m4rkovic.succulent_shop.response.CategoryResponse;
import com.m4rkovic.succulent_shop.service.CategoryService;
import com.m4rkovic.succulent_shop.validator.CategoryValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Validated
@Tag(name = "Category Controller", description = "Category management APIs")
@CrossOrigin
@Slf4j
public class CategoryApiController {

    private final CategoryService categoryService;
    private final CategoryValidator categoryValidator;

    // FIND BY ID
    @Operation(summary = "Get a category by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category found"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategory(
            @Parameter(description = "Category ID", required = true)
            @PathVariable Long id) {
        Category category = categoryService.findById(id);
        return ResponseEntity.ok(CategoryResponse.fromEntity(category));
    }

    // FIND ALL
    @Operation(summary = "Get all categories")
    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.Direction.fromString(sortDirection),
                sortBy
        );
        Page<Category> categoryPage = categoryService.findAllPaginated(pageable);
        Page<CategoryResponse> responsePage = categoryPage.map(CategoryResponse::fromEntity);
        return ResponseEntity.ok(responsePage);
    }

    // ADD CATEGORY
    @Operation(summary = "Create a new category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryDTO categoryDto) {
        log.debug("Creating new category with data: {}", categoryDto);

        try {
            // Validate using custom validator
            categoryValidator.validateAndThrow(categoryDto);

            Category savedCategory = categoryService.save(categoryDto);

            CategoryResponse response = CategoryResponse.fromEntity(savedCategory);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(savedCategory.getId())
                    .toUri();

            return ResponseEntity.created(location).body(response);

        } catch (IllegalArgumentException e) {
            log.error("Invalid data in category creation request", e);
            throw new InvalidDataException("Invalid category data: " + e.getMessage());
        }
    }

    // UPDATE
    @Operation(summary = "Update a category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @Parameter(description = "Category ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO categoryDto) {
        log.debug("Updating category {} with data: {}", id, categoryDto);

        categoryDto.setId(id);
        Category updatedCategory = categoryService.save(categoryDto);
        return ResponseEntity.ok(CategoryResponse.fromEntity(updatedCategory));
    }

    // DELETE
    @Operation(summary = "Delete a category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "Category ID", required = true)
            @PathVariable Long id) {
        log.debug("Deleting category: {}", id);
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}