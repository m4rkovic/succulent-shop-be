package com.m4rkovic.succulent_shop.response;

import com.m4rkovic.succulent_shop.entity.Product;
import com.m4rkovic.succulent_shop.enumerator.PotSize;
import com.m4rkovic.succulent_shop.enumerator.PotType;
import com.m4rkovic.succulent_shop.enumerator.ProductType;
import com.m4rkovic.succulent_shop.enumerator.ToolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private PlantResponse plant;
    private String productName;
    private String productDesc;
    private PotSize potSize;
    private boolean pot;
    private int potNumber;
    private PotType potType;
    private ProductType productType;
    private ToolType toolType;
    private BigDecimal price;
    private int quantity;
    private boolean active;
    private boolean onSale;

    public static ProductResponse fromEntity(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setPlant(PlantResponse.formEntity(product.getPlant()));
        response.setProductName(product.getProductName());
        response.setProductDesc(product.getProductDesc());
        response.setProductType(product.getProductType());
        response.setPotSize(product.getPotSize());
        response.setPotType(product.getPotType());
        response.setPot(product.isPot());
        response.setPotNumber(product.getPotNumber());
        response.setToolType(product.getToolType());
        response.setPrice(product.getPrice());
        response.setQuantity(product.getQuantity());
        response.setActive(product.isActive());
        response.setOnSale(product.isOnSale());
        return response;
    }
}