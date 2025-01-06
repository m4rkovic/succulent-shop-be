package com.m4rkovic.succulent_shop.controller;

import com.m4rkovic.succulent_shop.dto.PlantDTO;
import com.m4rkovic.succulent_shop.entity.Category;
import com.m4rkovic.succulent_shop.entity.Plant;
import com.m4rkovic.succulent_shop.enumerator.Color;
import com.m4rkovic.succulent_shop.exceptions.InvalidDataException;
import com.m4rkovic.succulent_shop.exceptions.ResourceNotFoundException;
import com.m4rkovic.succulent_shop.response.PlantResponse;
import com.m4rkovic.succulent_shop.service.CategoryService;
import com.m4rkovic.succulent_shop.service.PlantSearchCriteria;
import com.m4rkovic.succulent_shop.service.PlantSearchService;
import com.m4rkovic.succulent_shop.service.PlantService;
import com.m4rkovic.succulent_shop.validator.PlantValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/plants")
@RequiredArgsConstructor
@Tag(name = "Plant Controller", description = "Plant management APIs")
@CrossOrigin
@Validated
@Slf4j
public class PlantApiController {
    private final PlantService plantService;
    private final CategoryService categoryService;
    private final PlantSearchService plantSearchService;
    private final PlantValidator plantValidator;

    @Operation(summary = "Get a plant by ID!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plant found!"),
            @ApiResponse(responseCode = "404", description = "Plant not found!")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PlantResponse> getPlant(
            @Parameter(description = "Plant ID", required = true)
            @PathVariable Long id) {
        Plant plant = plantService.findById(id);
        return ResponseEntity.ok(PlantResponse.formEntity(plant));
    }

    // FIND ALL
    @Operation(summary = "Get all plants!")
    @GetMapping
    public ResponseEntity<Page<PlantResponse>> getPlants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.Direction.fromString(sortDirection),
                sortBy
        );
        Page<Plant> plantPage = plantService.findAllPaginated(pageable);
        Page<PlantResponse> responsePage = plantPage.map(PlantResponse::formEntity);
        return ResponseEntity.ok(responsePage);
    }

    // ADD PLANT
    @Operation(summary = "Create a new plant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plant created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<PlantResponse> createPlant(@Valid @RequestBody PlantDTO plantDto) {
        log.debug("Creating new plant with data: {}", plantDto);

        try {
            plantValidator.validateAndThrow(plantDto);

            Color primaryColor = plantDto.getPrimaryColor() != null ?
                    Color.valueOf(plantDto.getPrimaryColor().toUpperCase()) : null;
            Color secondaryColor = plantDto.getSecondaryColor() != null ?
                    Color.valueOf(plantDto.getSecondaryColor().toUpperCase()) : null;
            Color bloomColor = plantDto.getBloomColor() != null ?
                    Color.valueOf(plantDto.getBloomColor().toUpperCase()) : null;

            Category category = categoryService.findById(plantDto.getCategoryId());
            if (category == null) {
                throw new ResourceNotFoundException("Category not found with id: " + plantDto.getCategoryId());
            }

            Plant savedPlant = plantService.save(
                    plantDto.getName(),
                    plantDto.getCareInstructions(),
                    primaryColor,
                    secondaryColor,
                    bloomColor,
                    category
            );

            PlantResponse response = PlantResponse.formEntity(savedPlant);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(savedPlant.getId())
                    .toUri();

            return ResponseEntity.created(location).body(response);

        } catch (IllegalArgumentException e) {
            log.error("Invalid enum value in plant creation request", e);
            throw new InvalidDataException("Invalid plant data: " + e.getMessage());
        }
    }

    // UPDATE PLANT
    @Operation(summary = "Update a plant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plant updated successfully"),
            @ApiResponse(responseCode = "404", description = "Plant not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PlantResponse> updatePlant(
            @Parameter(description = "Plant ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody PlantDTO plantDto) {

        log.debug("Updating plant {} with data: {}", id, plantDto);

        try {
            plantValidator.validateAndThrow(plantDto);

            try {
                Color.valueOf(plantDto.getPrimaryColor().toUpperCase());
                Color.valueOf(plantDto.getSecondaryColor().toUpperCase());
                Color.valueOf(plantDto.getBloomColor().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException("Invalid color value: " + e.getMessage());
            }

            Plant updatedPlant = plantService.update(id, plantDto);
            return ResponseEntity.ok(PlantResponse.formEntity(updatedPlant));

        } catch (ResourceNotFoundException e) {
            log.error("Plant not found with id: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Error updating plant with id: {}", id, e);
            throw new InvalidDataException("Failed to update plant: " + e.getMessage());
        }
    }

    //DELETE
    @Operation(summary = "Delete a plant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Plant deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Plant not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlant(
            @Parameter(description = "Plant ID", required = true)
            @PathVariable Long id) {
        log.debug("Deleting plant: {}", id);
        plantService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    //SEARCH
    @GetMapping("/search")
    public ResponseEntity<Page<PlantResponse>> searchPlants(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String careInstructions,
            @RequestParam(required = false) String primaryColor,
            @RequestParam(required = false) String secondaryColor,
            @RequestParam(required = false) String bloomColor,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PlantSearchCriteria criteria = PlantSearchCriteria.builder()
                .searchTerm(searchTerm)
                .name(name)
                .careInstructions(careInstructions)
                .primaryColor(primaryColor != null ? Color.valueOf(primaryColor.toUpperCase()) : null)
                .secondaryColor(secondaryColor != null ? Color.valueOf(secondaryColor.toUpperCase()) : null)
                .secondaryColor(bloomColor != null ? Color.valueOf(bloomColor.toUpperCase()) : null)
                .categoryId(categoryId)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        Page<Plant> plantPage = plantSearchService.search(criteria, page, size);
        Page<PlantResponse> responsePage = plantPage.map(PlantResponse::formEntity);

        return ResponseEntity.ok(responsePage);
    }
}

