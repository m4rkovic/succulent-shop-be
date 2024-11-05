package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.exceptions.ResourceNotFoundException;
import com.m4rkovic.succulent_shop.dto.ProductDTO;
import com.m4rkovic.succulent_shop.entity.Plant;
import com.m4rkovic.succulent_shop.entity.Product;
import com.m4rkovic.succulent_shop.enumerator.PotSize;
import com.m4rkovic.succulent_shop.enumerator.PotType;
import com.m4rkovic.succulent_shop.enumerator.ProductType;
import com.m4rkovic.succulent_shop.enumerator.ToolType;
import com.m4rkovic.succulent_shop.exceptions.CreationException;
import com.m4rkovic.succulent_shop.exceptions.DeleteException;
import com.m4rkovic.succulent_shop.exceptions.ProductUpdateException;
import com.m4rkovic.succulent_shop.mapper.ProductMapper;
import com.m4rkovic.succulent_shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductValidationService validationService;
    private final ProductSearchService searchService;
    private final ProductMapper productMapper;

    // FIND ALL
    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        log.debug("Retrieving all products!");
        return productRepository.findAll();
    }

    // FIND BY ID
    @Override
    @Transactional(readOnly = true)
    public Product findById(Long id) {
        log.debug("Retrieving product with id: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Product not found with id: %d", id)));
    }

    // SAVE
    @Override
    @Transactional
    public Product save(Plant plant, String productName, String productDesc, PotSize potSize,
                        ProductType productType, boolean isPot, PotType potType, ToolType toolType,
                        int potNumber, BigDecimal price) {
        ProductDTO productDTO = createProductDTO(plant, productName, productDesc, potSize, productType,
                isPot, potType, toolType, potNumber, price);

        validationService.validateProductDTO(productDTO);

        try {
            Product product = productMapper.toEntity(productDTO);
            product.setPlant(plant);
            return productRepository.save(product);
        } catch (DataIntegrityViolationException e) {
            throw new CreationException("Failed to create product due to data integrity violation", e);
        }
    }

    // UPDATE
    @Override
    @Transactional
    public Product update(Long id, ProductDTO productDTO) {
        log.debug("Updating product with id: {}", id);
        Product existingProduct = findById(id);
        validationService.validateProductDTO(productDTO);

        try {
            productMapper.updateEntityFromDTO(existingProduct, productDTO);
            return productRepository.save(existingProduct);
        } catch (DataIntegrityViolationException e) {
            throw new ProductUpdateException(String.format("Failed to update product with id: %d", id), e);
        }
    }

    // DELETE BY ID
    @Override
    @Transactional
    public void deleteById(Long productId) {
        log.debug("Deleting product with id: {}", productId);
        findById(productId);

        try {
            productRepository.deleteById(productId);
        } catch (DataIntegrityViolationException e) {
            throw new DeleteException(
                    String.format("Cannot delete product with id: %d due to existing references", productId), e);
        }
    }

    // SEARCH
    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(ProductSearchCriteria criteria, int page, int size) {
        return searchService.search(criteria, page, size);
    }

    private ProductDTO createProductDTO(Plant plant, String productName, String productDesc,
                                        PotSize potSize, ProductType productType, boolean isPot,
                                        PotType potType, ToolType toolType, int potNumber, BigDecimal price) {
        return ProductDTO.builder()
                .productName(productName)
                .productDesc(productDesc)
                .potSize(potSize != null ? potSize.name() : null)
                .productType(productType != null ? productType.name() : null)
                .isPot(isPot)
                .potType(potType != null ? potType.name() : null)
                .toolType(toolType != null ? toolType.name() : null)
                .potNumber(potNumber)
                .price(price)
                .plantId(plant != null ? plant.getId() : null)
                .build();
    }
}
