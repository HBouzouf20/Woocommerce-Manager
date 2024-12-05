package com.hbdev.woocommerce_manager.services;

import com.hbdev.woocommerce_manager.models.Product;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();

    List<Product> getProductsByCategory(String category);

    List<Product> getProductsByName(String productName);


    Product insertProduct(Product product);

    Product updateProduct(String productName, Product product);


    List<Product> insertProducts(List<Product> products);

    Product findByImageUrl(String imageUrl);
}

