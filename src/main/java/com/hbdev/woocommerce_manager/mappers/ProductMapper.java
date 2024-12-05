package com.hbdev.woocommerce_manager.mappers;

import com.hbdev.woocommerce_manager.models.Product;
import com.hbdev.woocommerce_manager.models.WooCommerceProduct;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ProductMapper {

    // Factory Method to map Product to WooCommerceProduct
    public static WooCommerceProduct mapToWooCommerceProduct(Product product) throws IOException {
        // Here we use the Builder Pattern to create the WooCommerceProduct object

        return WooCommerceProduct.builder()
                .name(product.getName())
                .description(product.getDescription())
                .regularPrice(convertBigDecimalToString(parsePriceToBigDecimal(product.getPrice())))
                .salePrice(convertBigDecimalToString(parsePriceToBigDecimal(product.getPrice())))
                .status("publish") // Default status as 'publish'
                .type("simple") // Default type as 'simple'
                .manageStock(true) // Assuming stock management is enabled by default
                .stockQuantity(100) // Default stock quantity (can be set based on business rules)
                .images(List.of(Map.of("src", product.getImageUrl())))
                .build();
    }
    public static BigDecimal parsePriceToBigDecimal(String price) {
        // Remove the "DH" symbol and any surrounding whitespace
        if (price != null && price.contains("DH")) {
            String priceWithoutCurrency = price.replace("DH", "").replace("\u00A0", "").trim();
            try {
                // Convert the numeric part to BigDecimal
                return new BigDecimal(priceWithoutCurrency);
            } catch (NumberFormatException e) {
                // Handle invalid price format
                System.err.println("Invalid price format: " + price);
                return BigDecimal.ZERO; // Default value in case of error
            }
        }
        return BigDecimal.ZERO; // Default value if "DH" is not found
    }
    private static  String convertBigDecimalToString(BigDecimal price) {
        if (price != null) {
            return price.toPlainString(); // This ensures the string representation is exact without scientific notation
        }
        return "0.00"; // Default value if price is null
    }

}
