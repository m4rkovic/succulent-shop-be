package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.dto.BulkProductRequestDTO;
import com.m4rkovic.succulent_shop.exceptions.*;
import com.m4rkovic.succulent_shop.dto.ProductDTO;
import com.m4rkovic.succulent_shop.entity.Plant;
import com.m4rkovic.succulent_shop.entity.Product;
import com.m4rkovic.succulent_shop.enumerator.PotSize;
import com.m4rkovic.succulent_shop.enumerator.PotType;
import com.m4rkovic.succulent_shop.enumerator.ProductType;
import com.m4rkovic.succulent_shop.enumerator.ToolType;
import com.m4rkovic.succulent_shop.mapper.ProductMapper;
import com.m4rkovic.succulent_shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductValidationService validationService;
    private final ProductSearchService searchService;
    private final ProductMapper productMapper;
    private final FileStorageService fileStorageService;
    private final PlantService plantService;

    private static final BigDecimal SALE_DISCOUNT = new BigDecimal("0.20");
    private static final int SCALE = 2;

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        log.debug("Retrieving all products!");
        List<Product> products = productRepository.findAll();
        products.forEach(product -> {
            if (product.isOnSale()) {
                product.setPrice(calculateActualPrice(product.getPrice(), true));
            }
        });
        return products;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> findAllPaginated(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        productPage.forEach(product -> {
            if (product.isOnSale()) {
                product.setPrice(calculateActualPrice(product.getPrice(), true));
            }
        });
        return productPage;
    }

    @Override
    @Transactional(readOnly = true)
    public Product findById(Long id) {
        log.debug("Retrieving product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Product not found with id: %d", id)));

        if (product.isOnSale()) {
            product.setPrice(calculateActualPrice(product.getPrice(), true));
        }
        return product;
    }

    @Override
    @Transactional
    public Product save(Long id, Plant plant, String productName, String productDesc, PotSize potSize,
                        ProductType productType, boolean isPot, PotType potType, ToolType toolType,
                        int potNumber, BigDecimal price, Integer quantity, MultipartFile photoFile) {

        Product product = id != null ? findById(id) : new Product();
        String oldPhotoUrl = product.getPhotoUrl();

        try {
            // Update basic fields
            product.setPlant(plant);
            product.setProductName(productName);
            product.setProductDesc(productDesc);
            product.setPotSize(potSize);
            product.setProductType(productType);
            product.setPot(isPot);
            product.setPotType(potType);
            product.setToolType(toolType);
            product.setPotNumber(potNumber);
            product.setQuantity(quantity);

            // Handle photo file
            if (photoFile != null && !photoFile.isEmpty()) {
                try {
                    // Delete old photo if updating
                    if (oldPhotoUrl != null) {
                        fileStorageService.deleteFile(oldPhotoUrl);
                    }
                    String fileName = fileStorageService.storeFile(photoFile);
                    product.setPhotoUrl(fileName);
                } catch (Exception e) {
                    log.error("Failed to store photo file", e);
                    throw new FileStorageException("Failed to store photo file: " + e.getMessage());
                }
            }

            // Handle price and sale status
            if (product.isOnSale()) {
                product.setPrice(calculateActualPrice(price, true));
            } else {
                product.setPrice(price);
            }

            Product savedProduct = productRepository.save(product);
            log.debug("{} product with id: {}", id == null ? "Created" : "Updated", savedProduct.getId());
            return savedProduct;

        } catch (DataIntegrityViolationException e) {
            String operation = id == null ? "create" : "update";
            throw new InvalidDataException(String.format("Failed to %s product due to data integrity violation", operation), e);
        } catch (Exception e) {
            // Rollback photo changes if save fails
            if (photoFile != null && !photoFile.isEmpty() && oldPhotoUrl != null) {
                try {
                    fileStorageService.deleteFile(product.getPhotoUrl());
                    product.setPhotoUrl(oldPhotoUrl);
                } catch (Exception ex) {
                    log.error("Failed to rollback photo changes", ex);
                }
            }
            throw e;
        }
    }

    @Override
    @Transactional
    public Product save(Plant plant, String productName, String productDesc, PotSize potSize,
                        ProductType productType, boolean isPot, PotType potType, ToolType toolType,
                        int potNumber, BigDecimal price, Integer quantity, MultipartFile photoFile) {
        return save(null, plant, productName, productDesc, potSize, productType, isPot, potType,
                toolType, potNumber, price, quantity, photoFile);
    }

    @Override
    @Transactional
    public Product update(Long id, ProductDTO productDTO) {
        log.debug("Updating product with id: {}", id);
        validationService.validateProductDTO(productDTO);

        try {
            PotSize potSize = productDTO.getPotSize() != null ?
                    PotSize.valueOf(productDTO.getPotSize().toUpperCase()) : null;
            ProductType productType = ProductType.valueOf(productDTO.getProductType().toUpperCase());
            PotType potType = productDTO.getPotType() != null ?
                    PotType.valueOf(productDTO.getPotType().toUpperCase()) : null;
            ToolType toolType = productDTO.getToolType() != null ?
                    ToolType.valueOf(productDTO.getToolType().toUpperCase()) : null;

            Plant plant = null;
            if (productDTO.getPlantId() != null) {
                plant = plantService.findById(productDTO.getPlantId());
            }

            return save(
                    id,
                    plant,
                    productDTO.getProductName(),
                    productDTO.getProductDesc(),
                    potSize,
                    productType,
                    productDTO.isPot(),
                    potType,
                    toolType,
                    productDTO.getPotNumber(),
                    productDTO.getPrice(),
                    productDTO.getQuantity(),
                    productDTO.getPhotoFile()
            );
        } catch (IllegalArgumentException e) {
            log.error("Invalid enum value in product update request", e);
            throw new InvalidDataException("Invalid product data: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Product updateSaleStatus(Long id, boolean onSale) {
        log.debug("Updating sale status for product id: {} to: {}", id, onSale);
        Product product = findById(id);
        product.setOnSale(onSale);

        BigDecimal basePrice = onSale ?
                product.getPrice().divide(BigDecimal.ONE.subtract(SALE_DISCOUNT), SCALE, RoundingMode.HALF_UP) :
                product.getPrice();
        product.setPrice(calculateActualPrice(basePrice, onSale));

        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteById(Long productId) {
        log.debug("Deleting product with id: {}", productId);
        Product product = findById(productId);

        try {
            if (product.getPhotoUrl() != null) {
                try {
                    fileStorageService.deleteFile(product.getPhotoUrl());
                } catch (Exception e) {
                    log.error("Failed to delete photo file for product: {}", productId, e);
                }
            }
            productRepository.deleteById(productId);
        } catch (DataIntegrityViolationException e) {
            throw new DeleteException(
                    String.format("Cannot delete product with id: %d due to existing references", productId), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findProductsByIds(List<Long> productIds) {
        log.debug("Retrieving products with IDs: {}", productIds);
        Set<Long> uniqueProductIds = new HashSet<>(productIds);
        List<Product> uniqueProducts = productRepository.findAllById(uniqueProductIds);

        if (uniqueProducts.size() != uniqueProductIds.size()) {
            List<Long> foundIds = uniqueProducts.stream().map(Product::getId).toList();
            List<Long> missingIds = uniqueProductIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new ResourceNotFoundException(String.format("Products not found for IDs: %s", missingIds));
        }

        Map<Long, Product> productMap = uniqueProducts.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        return productIds.stream()
                .map(productMap::get)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<Product> bulkImport(List<BulkProductRequestDTO> products) {
        log.debug("Starting bulk import of {} products", products.size());
        List<Product> importedProducts = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < products.size(); i++) {
            BulkProductRequestDTO request = products.get(i);
            try {
                PotSize potSize = request.getPotSize() != null ?
                        PotSize.valueOf(request.getPotSize().toUpperCase()) : null;
                ProductType productType = ProductType.valueOf(request.getProductType().toUpperCase());
                PotType potType = request.getPotType() != null ?
                        PotType.valueOf(request.getPotType().toUpperCase()) : null;
                ToolType toolType = request.getToolType() != null ?
                        ToolType.valueOf(request.getToolType().toUpperCase()) : null;

                Product savedProduct = save(
                        request.getPlant(),
                        request.getProductName(),
                        request.getProductDesc(),
                        potSize,
                        productType,
                        request.isPot(),
                        potType,
                        toolType,
                        request.getPotNumber(),
                        request.getPrice(),
                        request.getQuantity(),
                        null
                );

                importedProducts.add(savedProduct);
            } catch (Exception e) {
                String errorMessage = String.format("Error importing product at index %d: %s", i, e.getMessage());
                errors.add(errorMessage);
                log.error(errorMessage, e);
            }
        }

        if (!errors.isEmpty()) {
            String errorMessage = String.format("Bulk import completed with %d errors: %s",
                    errors.size(), String.join("; ", errors));
            throw new BulkImportException(errorMessage, importedProducts);
        }

        log.debug("Bulk import completed successfully. Imported {} products", importedProducts.size());
        return importedProducts;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(ProductSearchCriteria criteria, int page, int size) {
        return searchService.search(criteria, page, size);
    }

    @Scheduled(cron = "0 0 3 * * ?") // Run at 3 AM every day
    @Transactional(readOnly = true)
    public void cleanupOrphanedFiles() {
        log.debug("Starting orphaned files cleanup");
        try {
            Set<String> dbFiles = productRepository.findAll().stream()
                    .map(Product::getPhotoUrl)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            fileStorageService.cleanupOrphanedFiles(dbFiles);
        } catch (Exception e) {
            log.error("Error during orphaned files cleanup", e);
        }
    }

    private BigDecimal calculateActualPrice(BigDecimal originalPrice, boolean isOnSale) {
        if (!isOnSale) {
            return originalPrice;
        }
        BigDecimal discount = originalPrice.multiply(SALE_DISCOUNT);
        return originalPrice.subtract(discount).setScale(SCALE, RoundingMode.HALF_UP);
    }
}