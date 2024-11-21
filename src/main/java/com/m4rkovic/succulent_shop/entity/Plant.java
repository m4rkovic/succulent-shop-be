package com.m4rkovic.succulent_shop.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.m4rkovic.succulent_shop.enumerator.Color;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "_plant")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Plant extends AbstractEntity {

    private String name;
    @Column(name = "plantPhoto", nullable = true)
    private String plantPhoto;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", nullable = false)
    private Category category;
    private Color primaryColor;
    private Color secondaryColor;
    @Nullable
    private Color bloomColor;
    @Column(length = 2000)
    private String careInstructions;

}
