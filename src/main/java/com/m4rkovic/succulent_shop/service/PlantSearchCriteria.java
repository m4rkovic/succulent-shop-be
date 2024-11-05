package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.enumerator.Color;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlantSearchCriteria {

    private String searchTerm;
    private String name;
    private Color primaryColor;
    private Color secondaryColor;
    private Color bloomColor;
    private String sortBy;
    private String sortDirection;
    private Long categoryId;
}