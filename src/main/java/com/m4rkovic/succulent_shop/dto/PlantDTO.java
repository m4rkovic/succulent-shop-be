package com.m4rkovic.succulent_shop.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlantDTO {

    private Long id;

    @JsonProperty("categoryId")
    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Plant name is required!")
    @Size(min = 2, max = 100, message = "Plant name must be between 2 and 100 characters!")
    private String name;

    private String plantPhoto;

    private MultipartFile photoFile;


    @NotBlank(message = "Primary color is required!")
    private String primaryColor;

    @NotBlank(message = "Secondary color is required!")
    private String secondaryColor;

    @JsonIgnore
    @NotBlank(message = "Bloom color is required!")
    private String bloomColor;
}
