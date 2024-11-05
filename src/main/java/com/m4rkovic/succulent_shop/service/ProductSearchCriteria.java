package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.enumerator.PotSize;
import com.m4rkovic.succulent_shop.enumerator.PotType;
import com.m4rkovic.succulent_shop.enumerator.ProductType;
import com.m4rkovic.succulent_shop.enumerator.ToolType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductSearchCriteria {
    private String searchTerm;
    private ProductType productType;
    private PotType potType;
    private ToolType toolType;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private PotSize potSize;
    private Boolean isPot;
    private String sortBy;
    private String sortDirection;
}
