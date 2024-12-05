package com.hbdev.woocommerce_manager.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Product {
    private String name;
    private String price;
    private String description;
    @JsonProperty("image_path")
    private String imageUrl;
}
