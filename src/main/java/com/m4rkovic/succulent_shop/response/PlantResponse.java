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
    private String careInstructions;

    public static PlantResponse formEntity(Plant plant) {
        // Return null if plant is null
        if (plant == null) {
            return null;
        }

        PlantResponse response = new PlantResponse();
        response.setId(plant.getId());
        response.setName(plant.getName());
        response.setPlantPhoto(plant.getPlantPhoto());
        // Handle potentially null category
        response.setCategory(plant.getCategory() != null ?
                CategoryResponse.fromEntity(plant.getCategory()) : null);
        response.setPrimaryColor(plant.getPrimaryColor());
        response.setSecondaryColor(plant.getSecondaryColor());
        response.setBloomColor(plant.getBloomColor());
        response.setCareInstructions(plant.getCareInstructions());
        return response;
    }
}