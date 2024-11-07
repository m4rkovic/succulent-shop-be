package com.m4rkovic.succulent_shop.dto;

import com.m4rkovic.succulent_shop.entity.Plant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkProductRequestDTO {
    private Plant plant;
    private String productName;
    private String productDesc;
    private String potSize;
    private String productType;
    private boolean isPot;
    private String potType;
    private String toolType;
    private int potNumber;
    private BigDecimal price;
}