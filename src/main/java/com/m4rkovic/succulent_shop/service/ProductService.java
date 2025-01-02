package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.dto.BulkProductRequestDTO;
import com.m4rkovic.succulent_shop.dto.ProductDTO;
import com.m4rkovic.succulent_shop.entity.Plant;
import com.m4rkovic.succulent_shop.entity.Product;
import com.m4rkovic.succulent_shop.enumerator.PotSize;
import com.m4rkovic.succulent_shop.enumerator.PotType;
import com.m4rkovic.succulent_shop.enumerator.ProductType;
import com.m4rkovic.succulent_shop.enumerator.ToolType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface ProductService {
    List<Product> findAll();

    Page<Product> findAllPaginated(Pageable pageable);

    Product findById(Long id);

    Product save(Long id, Plant plant, String productName, String productDesc, PotSize potSize,
                 ProductType productType, boolean isPot, PotType potType, ToolType toolType,
                 int potNumber, BigDecimal price, Integer quantity, MultipartFile photoFile);

    Product save(Plant plant, String productName, String productDesc, PotSize potSize,
                 ProductType productType, boolean isPot, PotType potType, ToolType toolType,
                 int potNumber, BigDecimal price, Integer quantity, MultipartFile photoFile);

    Product update(Long id, ProductDTO productDTO);

    Product updateSaleStatus(Long id, boolean onSale);

    void deleteById(Long productId);

    List<Product> bulkImport(List<BulkProductRequestDTO> products);

    List<Product> findProductsByIds(List<Long> productIds);

    Page<Product> searchProducts(ProductSearchCriteria criteria, int page, int size);
}