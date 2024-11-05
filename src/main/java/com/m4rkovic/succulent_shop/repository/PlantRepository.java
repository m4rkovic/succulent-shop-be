package com.m4rkovic.succulent_shop.repository;

import com.m4rkovic.succulent_shop.entity.Plant;
import com.m4rkovic.succulent_shop.enumerator.Color;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlantRepository extends JpaRepository<Plant, Long> {
    @Query("SELECT p FROM Plant p WHERE " +
            "(COALESCE(:searchTerm, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
            "(COALESCE(:name, '') = '' OR p.name = :name) AND " +
            "(:primaryColor IS NULL OR p.primaryColor = :primaryColor) AND " +
            "(:secondaryColor IS NULL OR p.primaryColor = :secondaryColor) AND " +
            "(:bloomColor IS NULL OR p.primaryColor = :bloomColor)")
    Page<Plant> findPlantsWithFilters(
            @Param("searchTerm") String searchTerm,
            @Param("name") String name,
            @Param("primaryColor") Color primaryColor,
            @Param("secondaryColor") Color secondaryColor,
            @Param("bloomColor") Color bloomColor,
            Pageable pageable
    );
}

