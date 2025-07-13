package com.hbdev.woocommerce_manager.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbdev.woocommerce_manager.exceptions.ElementNotFoundException;
import com.hbdev.woocommerce_manager.helpers.AppConstants;
import com.hbdev.woocommerce_manager.helpers.Tools;
import com.hbdev.woocommerce_manager.helpers.ZipHelper;
import com.hbdev.woocommerce_manager.models.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implementation of the ProductService that uses a cache and FileHelper to load product data.
 */
@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String PRODUCT_FOLDER_PATH = AppConstants.PRODUCT_FOLDER_PATH; // Set the correct path
    private static final ConcurrentHashMap<String, List<Product>> cache = new ConcurrentHashMap<>();

    /**
     * Caches the list of products from a folder if it doesn't already exist in the cache.
     *
     * @return A list of all products.
     */
    @Override
    public List<Product> getAllProducts() {
        return getProductsFromCache("allProducts");
    }


    /**
     * Caches the products based on the category if it doesn't already exist in the cache.
     *
     * @param category The category to filter products by.
     * @return A list of products from the given category.
     */
    @Override
    public List<Product> getProductsByCategory(String category) {
        return getProductsFromCache("category:" + category);
    }

    /**
     * Caches the products by name if they don't already exist in the cache.
     *
     * @param productName The product name to filter products by.
     * @return A list of products that match the search term.
     */
    @Override
    public List<Product> getProductsByName(String productName) {
        if (cache.isEmpty())
            getAllProducts();
        System.out.println("cache = " + cache);
        // Check if the cache already contains products for the given name
        List<Product> products = cache.get(productName);

        // If not in cache, retrieve from the database
        if (products == null) {
            System.out.println("Cache miss for product name: " + productName);
            products = getAllProducts().stream()
                    .filter(product -> product.getName().equalsIgnoreCase(productName))
                    .collect(Collectors.toList());

            // Store the result in the cache for future lookups
            cache.put(productName, products);
        } else {
            System.out.println("Cache hit for product name: " + productName);
        }

        return products;
    }

    /**
     * Inserts a new product into the list and caches the updated list.
     *
     * @param product The product to insert.
     * @return The newly inserted product.
     */
    @Override
    public Product insertProduct(Product product) {
        List<Product> products = getAllProducts();
        products.add(product);
        cache.put("allProducts", products);
        return product;
    }

    /**
     * Updates an existing product and caches the updated list.
     *
     * @param product The product to update.
     * @return The updated product.
     */
    @Override
    public Product updateProduct(String name, Product product) {
        List<Product> products = getAllProducts();
        Product productToUpdate = products.stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
        products.removeIf(p -> Objects.equals(p.getName(), name));
        products.add(productToUpdate);
        cache.put("allProducts", products);
        return product;
    }


    /**
     * Inserts multiple products into the list and caches the updated list.
     *
     * @param products A list of products to insert.
     * @return The list of inserted products.
     */
    @Override
    public List<Product> insertProducts(List<Product> products) {
        List<Product> allProducts = getAllProducts();
        allProducts.addAll(products);
        cache.put("allProducts", allProducts);
        return products;
    }

    @Override
    public Product findByImageUrl(String imageUrl) {
        return getAllProducts()
                .stream()
                .filter(p -> p.getImageUrl().toLowerCase().contains(imageUrl.toLowerCase()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Helper method to load products from the cache or from the file system.
     *
     * @param cacheKey The key for the cache.
     * @return The list of products.
     */
    private List<Product> getProductsFromCache(String cacheKey) {
        // Check if products are already cached
        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }

        // If not cached, load from the folder
        try {
            File zipPath = new ClassPathResource(PRODUCT_FOLDER_PATH).getFile();
            Map<String, byte[]> data = ZipHelper.readFilesFromZip(zipPath.getPath());
            List<Product> products = new ArrayList<>();
            log.warn("Inject products from cache !");
            data.forEach((key, value) -> {
                if (key.contains("json")) {
                    try {
                        Product product = objectMapper.readValue(value, Product.class);
                        //System.out.println("Image = " + product.getImageUrl());
                        System.out.println(product);
                        products.add(product);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            //List<Product> products = FileHelper.readProductsFromFolder(PRODUCT_FOLDER_PATH);
            cache.put(cacheKey, products);  // Cache the loaded products
            return products;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
