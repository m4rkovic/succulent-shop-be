package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.entity.Product;
import com.m4rkovic.succulent_shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

// Search Service
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSearchService {
    private final ProductRepository productRepository;

    public Page<Product> search(ProductSearchCriteria criteria, int page, int size) {
        log.debug("Searching products with criteria: {}", criteria);

        Sort sort = createSort(criteria);
        Pageable pageable = PageRequest.of(page, size, sort);

        return productRepository.findProductsWithFilters(
                criteria.getSearchTerm(),
                criteria.getProductType(),
                criteria.getPotType(),
                criteria.getToolType(),
                criteria.getMinPrice(),
                criteria.getMaxPrice(),
                criteria.getPotSize(),
                criteria.getIsPot(),
                criteria.getActive(),
                criteria.getOnSale(),
                criteria.getMinQuantity(),
                criteria.getMaxQuantity(),
                pageable
        );
    }

    private Sort createSort(ProductSearchCriteria criteria) {
        Sort.Direction direction = criteria.getSortDirection() == null ||
                criteria.getSortDirection().equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        String sortBy = criteria.getSortBy() == null ? "id" : criteria.getSortBy();

        return Sort.by(direction, sortBy);
    }
}
