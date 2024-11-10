package com.m4rkovic.succulent_shop.controller;

import com.m4rkovic.succulent_shop.dto.BulkProductRequestDTO;
import com.m4rkovic.succulent_shop.dto.ProductDTO;
import com.m4rkovic.succulent_shop.entity.Plant;
import com.m4rkovic.succulent_shop.entity.Product;
import com.m4rkovic.succulent_shop.enumerator.PotSize;
import com.m4rkovic.succulent_shop.enumerator.PotType;
import com.m4rkovic.succulent_shop.enumerator.ProductType;
import com.m4rkovic.succulent_shop.enumerator.ToolType;
import com.m4rkovic.succulent_shop.exceptions.BulkImportException;
import com.m4rkovic.succulent_shop.exceptions.InvalidDataException;
import com.m4rkovic.succulent_shop.exceptions.ResourceNotFoundException;
import com.m4rkovic.succulent_shop.response.BulkImportResponse;
import com.m4rkovic.succulent_shop.response.ProductResponse;
import com.m4rkovic.succulent_shop.service.PlantService;
import com.m4rkovic.succulent_shop.service.ProductSearchCriteria;
import com.m4rkovic.succulent_shop.service.ProductService;
import com.m4rkovic.succulent_shop.validator.ProductValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Validated
@Tag(name = "Product Controller", description = "Product management APIs")
@CrossOrigin
@Slf4j
public class ProductApiController {

    private final ProductService productService;
    private final PlantService plantService;
    private final ProductValidator productValidator;


    // FIND BY ID
    @Operation(summary = "Get a product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long id) {
        Product product = productService.findById(id);
        return ResponseEntity.ok(ProductResponse.fromEntity(product));
    }

    // FIND ALL
    @Operation(summary = "Get all products")
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.findAll()
                .stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    // ADD PRODUCT
    @Operation(summary = "Create a new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductDTO productDto) {
        log.debug("Creating new product with data: {}", productDto);

        try {
            productValidator.validateAndThrow(productDto);

            PotSize potSize = productDto.getPotSize() != null ?
                    PotSize.valueOf(productDto.getPotSize().toUpperCase()) : null;
            ProductType productType = ProductType.valueOf(productDto.getProductType().toUpperCase());
            PotType potType = productDto.getPotType() != null ?
                    PotType.valueOf(productDto.getPotType().toUpperCase()) : null;
            ToolType toolType = productDto.getToolType() != null ?
                    ToolType.valueOf(productDto.getToolType().toUpperCase()) : null;

            if (productDto.isPot() && productDto.getPotType() == null) {
                throw new InvalidDataException("Pot type is required when 'isPot' is true");
            }

            Plant plant = plantService.findById(productDto.getPlantId());
            if (plant == null) {
                throw new ResourceNotFoundException("Plant not found with id: " + productDto.getPlantId());
            }

            Product savedProduct = productService.save(
                    plant,
                    productDto.getProductName(),
                    productDto.getProductDesc(),
                    potSize,
                    productType,
                    productDto.isPot(),
                    potType,
                    toolType,
                    productDto.getPotNumber(),
                    productDto.getPrice()
            );

            ProductResponse response = ProductResponse.fromEntity(savedProduct);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(savedProduct.getId())
                    .toUri();

            return ResponseEntity.created(location).body(response);

        } catch (IllegalArgumentException e) {
            log.error("Invalid enum value in product creation request", e);
            throw new InvalidDataException("Invalid product data: " + e.getMessage());
        }
    }

    // UPDATE
    @Operation(summary = "Update a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO productDto) {
        log.debug("Updating product {} with data: {}", id, productDto);
        Product updatedProduct = productService.update(id, productDto);
        return ResponseEntity.ok(ProductResponse.fromEntity(updatedProduct));
    }

    @Operation(summary = "Bulk import products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Products imported successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "207", description = "Partial success - some products imported")
    })
    @PostMapping("/bulk")
    public ResponseEntity<?> bulkImportProducts(
            @Valid @RequestBody List<BulkProductRequestDTO> products) {
        log.debug("Received bulk import request for {} products", products.size());

        try {
            List<Product> importedProducts = productService.bulkImport(products);
            List<ProductResponse> responses = importedProducts.stream()
                    .map(ProductResponse::fromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new BulkImportResponse(
                            responses,
                            "All products imported successfully",
                            products.size(),
                            responses.size(),
                            Collections.emptyList()
                    ));

        } catch (BulkImportException e) {
            List<ProductResponse> partialResponses = e.getSuccessfulImports().stream()
                    .map(ProductResponse::fromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.MULTI_STATUS)
                    .body(new BulkImportResponse(
                            partialResponses,
                            "Partial import completed with errors",
                            products.size(),
                            partialResponses.size(),
                            Collections.singletonList(e.getMessage())
                    ));
        }
    }

    // DELETE
    @Operation(summary = "Delete a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long id) {
        log.debug("Deleting product: {}", id);
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search and filter products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved products"),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String productType,
            @RequestParam(required = false) String potType,
            @RequestParam(required = false) String toolType,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String potSize,
            @RequestParam(required = false) Boolean isPot,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        ProductSearchCriteria criteria = ProductSearchCriteria.builder()
                .searchTerm(searchTerm)
                .productType(productType != null ? ProductType.valueOf(productType.toUpperCase()) : null)
                .potType(potType != null ? PotType.valueOf(potType.toUpperCase()) : null)
                .toolType(toolType != null ? ToolType.valueOf(toolType.toUpperCase()) : null)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .potSize(potSize != null ? PotSize.valueOf(potSize.toUpperCase()) : null)
                .isPot(isPot)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        Page<Product> productPage = productService.searchProducts(criteria, page, size);
        Page<ProductResponse> responsePage = productPage.map(ProductResponse::fromEntity);

        return ResponseEntity.ok(responsePage);
    }
}