package com.hbdev.woocommerce_manager.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Product {
    private String name;
    private String price;
    private String description;
    @JsonProperty("image")
    private String imageUrl;
    private String category;
    private String brand;
}
