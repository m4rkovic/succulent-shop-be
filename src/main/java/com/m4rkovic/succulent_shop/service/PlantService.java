package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.dto.PlantDTO;
import com.m4rkovic.succulent_shop.entity.Category;
import com.m4rkovic.succulent_shop.entity.Plant;
import com.m4rkovic.succulent_shop.enumerator.Color;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface PlantService {
    public List<Plant> findAll();

    public Plant findById(Long id);

    public Page<Plant> findAllPaginated(Pageable pageable);


    public Plant save(String name, String careInstructions, Color primaryColor,
                      Color secondaryColor, Color bloomColor, Category category);

    @Transactional
    Plant update(Long id, PlantDTO plantDto);

    public void deleteById(Long plantId);
}
