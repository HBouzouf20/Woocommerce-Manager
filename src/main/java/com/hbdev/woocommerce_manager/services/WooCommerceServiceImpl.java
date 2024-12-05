package com.hbdev.woocommerce_manager.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbdev.woocommerce_manager.helpers.ApiHelper;
import com.hbdev.woocommerce_manager.helpers.AppConstants;
import com.hbdev.woocommerce_manager.models.WooCommerceConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Implementation of WooCommerceService using ApiHelper and WooCommerceConnection.
 *
 * @param <T> The type of resource to manage (e.g., Product, Order).
 */
@Service
@Slf4j
public class WooCommerceServiceImpl<T> implements WooCommerceService<T> {

    private final ApiHelper<T> apiHelper;
    private final ObjectMapper objectMapper;
    private static final  String ENDPOINT = AppConstants.PRODUCT_ENDPOINT;
    private static final  String MEDIA_ENDPOINT = AppConstants.MEDIA_ENDPOINT;
    private WooCommerceConnection connection;
    /**
     * Constructor for WooCommerceServiceImpl.
     *
     */
    public WooCommerceServiceImpl() {
        this.apiHelper = new ApiHelper<>();
        this.objectMapper = new ObjectMapper(); // JSON parser
    }


    @Override
    public void connection(WooCommerceConnection connection) {
        this.connection = connection;
        log.info("WooCommerce connection created ...");
    }

    @Override
    public T create(T resource) throws Exception {
        // Build the URL for the API endpoint
        String url = buildUrl();

        // Get the authentication headers
        Map<String, String> headers = connection.getAuthHeaders();
        // Attempt to send the POST request and return the result
        try {
            return apiHelper.post(url, headers, resource);
        } catch (Exception e) {
            // Log or handle specific errors here
            throw new Exception("Failed to create resource: " + e.getMessage(), e);
        }
    }

    @Override
    public T read(String id) throws Exception {
        String url = buildUrl() + "/" + id;
        Map<String, String> headers = connection.getAuthHeaders();
        return apiHelper.get(url, headers);
    }

    @Override
    public T update( String id, T resource) throws Exception {
        String url = buildUrl() + "/" + id;
        Map<String, String> headers = connection.getAuthHeaders();
        return apiHelper.put(url, headers, resource);
    }

    @Override
    public boolean delete(String id) throws Exception {
        String url = buildUrl() + "/" + id;
        Map<String, String> headers = connection.getAuthHeaders();
        apiHelper.delete(url, headers);
        return true;
    }

    @Override
    public void uploadImage(byte[] imageBytes, String fileName) throws Exception {
        String uploadUrl = buildMediaUrl();
        HttpURLConnection connection = (HttpURLConnection) new URL(uploadUrl).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        Map<String, String> headers = this.connection.getMediaAuthHeaders();
        headers.forEach(connection::setRequestProperty);

        connection.setRequestProperty("Content-Type", "image/jpg");
        connection.setRequestProperty("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        System.out.println("connection = " + connection);

        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(imageBytes);
        }

        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode); // Debug response code
        if (responseCode == HttpURLConnection.HTTP_CREATED) {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseJson = objectMapper.readValue(connection.getInputStream(), Map.class);
            System.out.println("Response JSON: " + responseJson); // Debug response body
            responseJson.get("source_url");
        } else {
            try (InputStream errorStream = connection.getErrorStream()) {
                if (errorStream != null) {
                    String responseBody = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                    throw new RuntimeException("Image upload failed. Response code: " + responseCode + ". Response body: " + responseBody);
                }
            }
        }
    }


    // Helper method to build the URL
    private String buildUrl() {
        return connection.getBaseUrl() + ENDPOINT;
    }
    private String buildMediaUrl() {
        return connection.getBaseUrl() + MEDIA_ENDPOINT;
    }
}
