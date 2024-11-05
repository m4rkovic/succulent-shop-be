package com.m4rkovic.succulent_shop.repository;

import com.m4rkovic.succulent_shop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import com.m4rkovic.succulent_shop.enumerator.ProductType;
import com.m4rkovic.succulent_shop.enumerator.PotType;
import com.m4rkovic.succulent_shop.enumerator.ToolType;
import com.m4rkovic.succulent_shop.enumerator.PotSize;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE " +
            "(:searchTerm IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.productDesc) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
            "(:productType IS NULL OR p.productType = :productType) AND " +
            "(:potType IS NULL OR p.potType = :potType) AND " +
            "(:toolType IS NULL OR p.toolType = :toolType) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:potSize IS NULL OR p.potSize = :potSize) AND " +
            "(:isPot IS NULL OR p.isPot = :isPot)")
    Page<Product> findProductsWithFilters(
            @Param("searchTerm") String searchTerm,
            @Param("productType") ProductType productType,
            @Param("potType") PotType potType,
            @Param("toolType") ToolType toolType,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("potSize") PotSize potSize,
            @Param("isPot") Boolean isPot,
            Pageable pageable
    );
}
