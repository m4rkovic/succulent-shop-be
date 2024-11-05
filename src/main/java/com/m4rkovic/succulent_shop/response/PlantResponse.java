package com.m4rkovic.succulent_shop.response;

import com.m4rkovic.succulent_shop.entity.Plant;
import com.m4rkovic.succulent_shop.enumerator.Color;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantResponse {
    private Long id;
    private String name;
    private String plantPhoto;
    private CategoryResponse category;
    private Color primaryColor;
    private Color secondaryColor;
    private Color bloomColor;

    public static PlantResponse formEntity(Plant plant) {
        PlantResponse response = new PlantResponse();
        response.setId(plant.getId());
        response.setName(plant.getName());
        response.setPlantPhoto(plant.getPlantPhoto());
        response.setCategory(CategoryResponse.fromEntity(plant.getCategory()));
        response.setPrimaryColor(plant.getPrimaryColor());
        response.setSecondaryColor(plant.getSecondaryColor());
        response.setBloomColor(plant.getBloomColor());
        return response;
    }
}