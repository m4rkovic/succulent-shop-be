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
//    private UserResponse user;  // Assuming you have a UserResponse class
    private ProductResponse product;  // Assuming you have a ProductResponse class
    private int score;
    private String comment;
    private Date createdDate;

    public static RatingResponse fromEntity(Rating rating) {
        return RatingResponse.builder()
                .id(rating.getId())
//                .user(UserResponse.fromEntity(rating.getUser()))  // Convert User entity to UserResponse
                .product(ProductResponse.fromEntity(rating.getProduct()))  // Convert Product entity to ProductResponse
                .score(rating.getScore())
                .comment(rating.getComment())
                .createdDate(rating.getCreatedDate())
                .build();
    }
}