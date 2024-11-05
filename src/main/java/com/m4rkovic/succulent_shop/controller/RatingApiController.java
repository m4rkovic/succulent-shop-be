package com.m4rkovic.succulent_shop.controller;

import com.m4rkovic.succulent_shop.dto.RatingDTO;
import com.m4rkovic.succulent_shop.entity.Product;
import com.m4rkovic.succulent_shop.entity.Rating;
import com.m4rkovic.succulent_shop.entity.User;
import com.m4rkovic.succulent_shop.exceptions.ResourceNotFoundException;
import com.m4rkovic.succulent_shop.response.RatingResponse;
import com.m4rkovic.succulent_shop.service.ProductService;
import com.m4rkovic.succulent_shop.service.RatingService;
import com.m4rkovic.succulent_shop.service.UserService;
import com.m4rkovic.succulent_shop.validator.RatingValidator;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.m4rkovic.succulent_shop.exceptions.InvalidDataException;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
@Validated
@Tag(name = "Rating Controller", description = "Rating management APIs")
@CrossOrigin
@Slf4j
public class RatingApiController {

    private final RatingService ratingService;
    private final UserService userService;
    private final ProductService productService;
    private final RatingValidator ratingValidator;

    // ADD RATING
    @Operation(summary = "Create a new rating")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rating created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User or Product not found")
    })
    @PostMapping
    public ResponseEntity<RatingResponse> createRating(@Valid @RequestBody RatingDTO ratingDto) {
        log.debug("Creating new rating with data: {}", ratingDto);

        try {
            ratingValidator.validateAndThrow(ratingDto);

            User user = userService.findById(ratingDto.getUserId());
            if (user == null) {
                throw new ResourceNotFoundException("User not found with id: " + ratingDto.getUserId());
            }

            Product product = productService.findById(ratingDto.getProductId());
            if (product == null) {
                throw new ResourceNotFoundException("Product not found with id: " + ratingDto.getProductId());
            }

            Rating savedRating = ratingService.save(
                    user,
                    product,
                    ratingDto.getScore(),
                    ratingDto.getComment(),
                    new Date()
            );

            RatingResponse response = RatingResponse.fromEntity(savedRating);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(savedRating.getId())
                    .toUri();

            return ResponseEntity.created(location).body(response);

        } catch (IllegalArgumentException e) {
            log.error("Invalid data in rating creation request", e);
            throw new InvalidDataException("Invalid rating data: " + e.getMessage());
        }
    }

    // DELETE RATING
    @Operation(summary = "Delete a rating")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rating deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Rating not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(
            @Parameter(description = "Rating ID", required = true)
            @PathVariable Long id) {
        log.debug("Deleting rating: {}", id);
        ratingService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // GET RATINGS BY PRODUCT
    @Operation(summary = "Get ratings for a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ratings found"),
            @ApiResponse(responseCode = "404", description = "No ratings found for the product")
    })
    @GetMapping("/products/{productId}")
    public ResponseEntity<List<RatingResponse>> getRatingsByProduct(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long productId) {
        log.debug("Fetching ratings for product: {}", productId);
        List<RatingResponse> ratings = ratingService.findByProductId(productId)
                .stream()
                .map(RatingResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ratings);
    }
}
