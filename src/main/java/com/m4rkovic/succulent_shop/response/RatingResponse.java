package com.m4rkovic.succulent_shop.response;

import com.m4rkovic.succulent_shop.entity.Rating;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponse {
    private Long id;
    private ProductResponse product;
    private int score;
    private String comment;
    private Date createdDate;

    public static RatingResponse fromEntity(Rating rating) {
        return RatingResponse.builder()
                .id(rating.getId())
                .product(ProductResponse.fromEntity(rating.getProduct()))
                .score(rating.getScore())
                .comment(rating.getComment())
                .createdDate(rating.getCreatedDate())
                .build();
    }
}