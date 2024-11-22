package com.m4rkovic.succulent_shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingDTO {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @Min(value = 1, message = "Rating score must be at least 1")
    @Max(value = 5, message = "Rating score must not exceed 5")
    private int score;

    @Size(max = 500, message = "Comment cannot exceed 500 characters")
    private String comment;

}