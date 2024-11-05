package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.dto.RatingDTO;
import com.m4rkovic.succulent_shop.entity.Product;
import com.m4rkovic.succulent_shop.entity.Rating;
import com.m4rkovic.succulent_shop.entity.User;
import com.m4rkovic.succulent_shop.exceptions.CreationException;
import com.m4rkovic.succulent_shop.exceptions.DeleteException;
import com.m4rkovic.succulent_shop.exceptions.ResourceNotFoundException;
import com.m4rkovic.succulent_shop.mapper.RatingMapper;
import com.m4rkovic.succulent_shop.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingService {
    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;
    private final RatingValidationService validationService;

    // FIND ALL
    @Transactional(readOnly = true)
    public List<Rating> findAll() {
        log.debug("Retrieving all ratings!");
        return ratingRepository.findAll();
    }

    // FIND BY ID
    @Transactional(readOnly = true)
    public Rating findById(Long id) {
        log.debug("Retrieving rating with id: {}", id);
        return ratingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Rating not found with id: %d", id)));
    }

    // FIND BY PRODUCT ID

    @Transactional(readOnly = true)
    public List<Rating> findByProductId(Long productId) {
        log.debug("Retrieving ratings for product id: {}", productId);
        return ratingRepository.findByProductId(productId);
    }

    // SAVE
    @Transactional
    public Rating save(User user, Product product, int score, String comment, Date createdDate) {

        RatingDTO ratingDTO = createRatingDTO(product, user, score, comment, createdDate);
        validationService.validateRatingDto(ratingDTO);

        try {
            Rating rating = ratingMapper.toEntity(ratingDTO);
            rating.setUser(user);
            rating.setProduct(product);
            rating.setCreatedDate(new Date());

            return ratingRepository.save(rating);
        } catch (DataIntegrityViolationException e) {
            throw new CreationException("Failed to create rating due to data integrity violation", e);
        }
    }


    // DELETE BY ID
    @Transactional
    public void deleteById(Long ratingId) {
        log.debug("Deleting rating with id: {}", ratingId);
        findById(ratingId);

        try {
            ratingRepository.deleteById(ratingId);
        } catch (DataIntegrityViolationException e) {
            throw new DeleteException(
                    String.format("Cannot delete rating with id: %d due to existing references", ratingId), e);
        }
    }

    private RatingDTO createRatingDTO(Product product, User user, int score, String comment, Date createdDate) {
        return RatingDTO.builder()
                .productId(product != null ? product.getId() : null)
                .userId(user != null ? user.getId() : null)
                .score(score)
                .comment(comment)
                .createdDate(createdDate != null ? createdDate : new Date())
                .build();
    }
}