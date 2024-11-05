package com.m4rkovic.succulent_shop.mapper;

import com.m4rkovic.succulent_shop.dto.RatingDTO;
import com.m4rkovic.succulent_shop.entity.Rating;
import com.m4rkovic.succulent_shop.entity.Product;
import com.m4rkovic.succulent_shop.entity.User;
import com.m4rkovic.succulent_shop.service.ProductService;
import com.m4rkovic.succulent_shop.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class RatingMapper {

    private final ProductService productService;
    private final UserService userService;

    public Rating toEntity(RatingDTO dto) {
        if (dto == null) {
            return null;
        }

        Product product = dto.getProductId() != null ? productService.findById(dto.getProductId()) : null;
        User user = dto.getUserId() != null ? userService.findById(dto.getUserId()) : null;

        return Rating.builder()
                .product(product)
                .user(user)
                .score(dto.getScore())
                .comment(dto.getComment())
                .createdDate(dto.getCreatedDate() != null ? dto.getCreatedDate() : new Date())
                .build();
    }

    public RatingDTO toDTO(Rating entity) {
        if (entity == null) {
            return null;
        }

        return RatingDTO.builder()
                .productId(entity.getProduct() != null ? entity.getProduct().getId() : null)
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .score(entity.getScore())
                .comment(entity.getComment())
                .createdDate(entity.getCreatedDate())
                .build();
    }

    public void updateEntityFromDTO(Rating entity, RatingDTO dto) {
        if (entity == null || dto == null) {
            return;
        }

        if (dto.getScore() != 0) {
            entity.setScore(dto.getScore());
        }
        if (dto.getComment() != null) {
            entity.setComment(dto.getComment());
        }
        if (dto.getProductId() != null) {
            entity.setProduct(productService.findById(dto.getProductId()));
        }
        if (dto.getUserId() != null) {
            entity.setUser(userService.findById(dto.getUserId()));
        }
        if (dto.getCreatedDate() != null) {
            entity.setCreatedDate(dto.getCreatedDate());
        }
    }

    public List<RatingDTO> toDTOList(List<Rating> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
