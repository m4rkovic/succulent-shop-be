package com.m4rkovic.succulent_shop.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name="_category")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Category extends AbstractEntity {

    private String categoryName;

    private String categoryDesc;
}
