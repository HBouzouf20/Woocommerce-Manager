package com.hbdev.woocommerce_manager.services;

import com.hbdev.woocommerce_manager.mappers.ProductMapper;
import com.hbdev.woocommerce_manager.models.WooCommerceConnection;
import com.hbdev.woocommerce_manager.models.WooCommerceProduct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Arrays;


@AllArgsConstructor
@Service
public class WooCommerceProductService {

    private final WooCommerceService<WooCommerceProduct> wooCommerceService;
    private final WordpressService wordpressService;
    private final ProductService productService;

    public void connection(WooCommerceConnection connection) {
        wooCommerceService.connection(connection);
    }
    public void insertProducts() {
        productService.getAllProducts().forEach(product -> {
            try {
                this.create(ProductMapper.mapToWooCommerceProduct(product));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void create(WooCommerceProduct product)  {
        // Implement logic to create a product in WooCommerce via API
        try {
            System.out.println(MessageFormat.format("Inserting product {0} ...", product.getName()));
            wooCommerceService.create(product);
            System.out.println("Product inserted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public void uploadImage(byte[] image, String fileName) throws Exception {
        // Implement logic to read a product from WooCommerce by ID
        System.out.println("Inserting image ...");
        wordpressService.uploadImage(image, fileName);
        System.out.println("Image inserted successfully");

    }

    public WooCommerceProduct read(String id) throws Exception {
        // Implement logic to read a product from WooCommerce by ID
        return wooCommerceService.read(id);
    }

    public WooCommerceProduct update(String id, WooCommerceProduct product) throws Exception {
        // Implement logic to update a product in WooCommerce via API
        return wooCommerceService.update(id, product);
    }

    public boolean delete(String id) throws Exception {
        // Implement logic to delete a product from WooCommerce by ID
        return wooCommerceService.delete(id);
    }
}
