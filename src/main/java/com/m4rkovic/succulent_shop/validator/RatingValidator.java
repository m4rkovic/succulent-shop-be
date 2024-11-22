package com.m4rkovic.succulent_shop.validator;

import com.m4rkovic.succulent_shop.dto.RatingDTO;
import com.m4rkovic.succulent_shop.entity.Rating;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class RatingValidator {

    private static final int MIN_SCORE = 1;
    private static final int MAX_SCORE = 5;
    private static final int MAX_COMMENT_LENGTH = 1000;

    public List<String> validate(RatingDTO ratingDTO) {
        List<String> violations = new ArrayList<>();

        validateBasicFields(ratingDTO, violations);
        validateRelations(ratingDTO, violations);

        return violations;
    }

    private void validateBasicFields(RatingDTO ratingDTO, List<String> violations) {
        // Enhanced score validation
        validateScore(ratingDTO.getScore(), violations);

        // Validate comment if present
        if (StringUtils.isNotBlank(ratingDTO.getComment())) {
            if (ratingDTO.getComment().length() > MAX_COMMENT_LENGTH) {
                violations.add(String.format("Comment cannot exceed %d characters!", MAX_COMMENT_LENGTH));
            }
        }
    }

    private void validateScore(int score, List<String> violations) {
        if (score < MIN_SCORE || score > MAX_SCORE) {
            violations.add(String.format("Invalid rating score: %d. Score must be between %d and %d stars!",
                    score, MIN_SCORE, MAX_SCORE));
        }
    }

    private void validateRelations(RatingDTO ratingDTO, List<String> violations) {
        // Validate user
        if (ratingDTO.getUserId() == null) {
            violations.add("User cannot be null!");
        }

        // Validate product
        if (ratingDTO.getProductId() == null) {
            violations.add("Product cannot be null!");
        }
    }

    public void validateAndThrow(RatingDTO ratingDTO) {
        List<String> violations = validate(ratingDTO);
        if (!violations.isEmpty()) {
            throw new ValidationException(String.join(", ", violations));
        }
    }

    // Custom exception class for validation
    public static class ValidationException extends RuntimeException {
        public ValidationException(String message) {
            super(message);
        }
    }
}