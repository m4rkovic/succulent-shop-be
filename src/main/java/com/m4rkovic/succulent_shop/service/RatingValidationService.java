package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.dto.RatingDTO;
import com.m4rkovic.succulent_shop.exceptions.ValidationException;
import com.m4rkovic.succulent_shop.validator.RatingValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingValidationService {
    private final RatingValidator ratingValidator;

    public void validateRatingDto(RatingDTO ratingDTO) {
        if (ratingDTO == null) {
            throw new ValidationException("Rating cannot be null!");
        }

        List<String> violations = ratingValidator.validate(ratingDTO);
        if (!violations.isEmpty()) {
            throw new ValidationException("Rating validation failed ", violations);
        }
    }
}
