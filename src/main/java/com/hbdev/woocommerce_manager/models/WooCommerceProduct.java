package com.hbdev.woocommerce_manager.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class WooCommerceProduct {

    @JsonProperty("name")
    private String name;

    @JsonProperty("slug")
    private String slug;

    @JsonProperty("description")
    private String description;

    @JsonProperty("short_description")
    private String shortDescription;

    @JsonProperty("regular_price")  // Use @JsonProperty to map to the API field name
    private String regularPrice;

    @JsonProperty("sale_price")  // Use @JsonProperty to map to the API field name
    private String salePrice;

    @JsonProperty("sku")
    private String sku;

    @JsonProperty("manage_stock")
    private boolean manageStock;

    @JsonProperty("stock_quantity")
    private int stockQuantity;

    @JsonProperty("status")
    private String status;

    @JsonProperty("type")
    private String type;

    @JsonProperty("categories")
    private Category[] categories;

    @JsonProperty("images")
    private List<Map<String, String>> images;

    // Constructor
    public WooCommerceProduct(String name, String description, BigDecimal regularPrice, String sku) {
        this.name = name;
        this.description = description;
        this.sku = sku;
        this.status = "publish"; // default status
        this.type = "simple"; // default product type
        this.manageStock = true; // default manage stock
    }
}
