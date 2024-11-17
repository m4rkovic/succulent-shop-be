package com.m4rkovic.succulent_shop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.m4rkovic.succulent_shop.enumerator.PotSize;
import com.m4rkovic.succulent_shop.enumerator.PotType;
import com.m4rkovic.succulent_shop.enumerator.ProductType;
import com.m4rkovic.succulent_shop.enumerator.ToolType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@SuperBuilder
@Table(name = "_product")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Plant plant;

    @Column(nullable = false)
    private String productName;

    @Column(columnDefinition = "TEXT")
    private String productDesc;

    @Column(nullable = true)
    private PotSize potSize;
    private boolean isPot;

    @Column(nullable = true)
    private int potNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private PotType potType;

    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private ToolType toolType;


    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean onSale = false;

    @ManyToMany(mappedBy = "products")
    private List<Order> orders = new ArrayList<>();

    @Column(name = "photo_url", nullable = true)
    private String photoUrl;
    private BigDecimal price;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Rating> ratings = new ArrayList<>();
}
