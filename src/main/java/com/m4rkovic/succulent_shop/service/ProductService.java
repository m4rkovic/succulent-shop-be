package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.dto.ProductDTO;
import com.m4rkovic.succulent_shop.entity.Plant;
import com.m4rkovic.succulent_shop.entity.Product;
import com.m4rkovic.succulent_shop.enumerator.PotSize;
import com.m4rkovic.succulent_shop.enumerator.PotType;
import com.m4rkovic.succulent_shop.enumerator.ProductType;
import com.m4rkovic.succulent_shop.enumerator.ToolType;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface ProductService {

    public List<Product> findAll();

    public Product findById(Long id);

    public Product save(Plant plant, String productName, String productDesc, PotSize potSize, ProductType productType, boolean isPot, PotType potType, ToolType toolType, int potNumber, BigDecimal price);

    Product update(Long id, ProductDTO productDTO);

    public void deleteById(Long productId);

    public List<Product> findProductsByIds(List<Long> productIds);
    Page<Product> searchProducts(ProductSearchCriteria criteria, int page, int size);
}
