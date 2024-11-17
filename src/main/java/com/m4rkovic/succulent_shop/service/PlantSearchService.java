package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.entity.Plant;
import com.m4rkovic.succulent_shop.repository.PlantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class PlantSearchService {

        private final PlantRepository plantRepository;

        public Page<Plant> search(PlantSearchCriteria criteria, int page, int size) {
            log.debug("Searching products with criteria: {}", criteria);

            Sort sort = createSort(criteria);
            Pageable pageable = PageRequest.of(page, size, sort);

            return plantRepository.findPlantsWithFilters(
                    criteria.getSearchTerm(),
                    criteria.getName(),
                    criteria.getPrimaryColor(),
                    criteria.getSecondaryColor(),
                    criteria.getBloomColor(),
                    criteria.getCareInstructions(),
                    pageable
            );
        }


        private Sort createSort(PlantSearchCriteria criteria) {
            Sort.Direction direction = criteria.getSortDirection() == null ||
                    criteria.getSortDirection().equalsIgnoreCase("asc")
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC;

            String sortBy = criteria.getSortBy() == null ? "id" : criteria.getSortBy();

            return Sort.by(direction, sortBy);
        }
    }
