package com.hbdev.woocommerce_manager.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbdev.woocommerce_manager.helpers.AppConstants;
import com.hbdev.woocommerce_manager.helpers.FileHelper;
import com.hbdev.woocommerce_manager.helpers.ZipHelper;
import com.hbdev.woocommerce_manager.mappers.ProductMapper;
import com.hbdev.woocommerce_manager.models.Product;
import com.hbdev.woocommerce_manager.models.WooCommerceConnection;
import com.hbdev.woocommerce_manager.models.WordpressConnection;
import com.hbdev.woocommerce_manager.services.ProductService;
import com.hbdev.woocommerce_manager.services.WooCommerceProductService;
import com.hbdev.woocommerce_manager.services.WordpressService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class Runner implements CommandLineRunner {
    private final WooCommerceProductService wooCommerceProductService;
    private final WordpressService wordpressService;
    private final ProductService productService;

    public Runner(WooCommerceProductService wooCommerceProductService, WordpressService wordpressService, ObjectMapper objectMapper, ProductService productService) {
        this.wooCommerceProductService = wooCommerceProductService;
        this.wordpressService = wordpressService;
        this.productService = productService;
    }

    @Override
    public void run(String... args) throws Exception {
        WooCommerceConnection connection = WooCommerceConnection.builder()
                .baseUrl(AppConstants.BASE_URL)
                .consumerKey("ck_f21dded9be5abe09d8159fea49c91fadbb276d0b")
                .consumerSecret("cs_df3a82062e55a674d1f9c8efdbce02ad78cba7fe")
                .build();

        WordpressConnection wordpressConnection = WordpressConnection.builder()
                .username("treasortv@gmail.com")
                .password("JW6%0zD56Qm64U%vo*s46^FO")
                .build();
        wooCommerceProductService.connection(connection);
        wordpressService.connection(wordpressConnection);

        //wooCommerceProductService.insertProducts();
        File zipPath = new ClassPathResource(AppConstants.PRODUCT_FOLDER_PATH).getFile();
        Map<String, byte[]> data = ZipHelper.readFilesFromZip(zipPath.getPath());
        data.forEach((key, value) -> {
            if (key.contains("jpg")|| key.contains(".png") || key.contains("jpeg") || key.contains("gif")) {
                try {
                    String fileName = String.valueOf(Path.of(key).getFileName());
                    String imageUrl = wordpressService.uploadImage(value, fileName);
                    log.info("Image uploaded: {}", imageUrl);
                    Product product = productService.findByImageUrl(fileName);
                    log.warn("Injection of product : {}", product != null ? product.getName() : null);
                    if (product != null) {
                        log.info("Product found: {}", product);
                        product.setImageUrl(imageUrl);
                        productService.updateProduct(product.getName(), product);
                        System.out.println("product = " + product);
                        wooCommerceProductService.create(ProductMapper.mapToWooCommerceProduct(product));

                    }
                                   } catch (Exception e) {
                    System.out.println(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });
        //System.out.println("productService = " + productService.getAllProducts());
    }
}
