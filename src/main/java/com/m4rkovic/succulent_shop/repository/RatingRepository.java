package com.m4rkovic.succulent_shop.repository;

import com.m4rkovic.succulent_shop.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByProductId(Long productId); // Method to find ratings by product ID
}
