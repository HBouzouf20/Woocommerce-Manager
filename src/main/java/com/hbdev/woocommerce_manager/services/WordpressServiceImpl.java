package com.hbdev.woocommerce_manager.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbdev.woocommerce_manager.helpers.AppConstants;
import com.hbdev.woocommerce_manager.models.WordpressConnection;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@Slf4j
public class WordpressServiceImpl implements WordpressService {
    private WordpressConnection connection;
    private static final  String MEDIA_ENDPOINT = AppConstants.MEDIA_ENDPOINT;

    @Override
    public void connection(WordpressConnection connection) {
        this.connection = connection;
    }

    @Override
    public String uploadImage(byte[] imageBytes, String fileName) throws Exception { //Add define('ALLOW_UNFILTERED_UPLOADS', true); in wp-config.php
        String uploadUrl = buildMediaUrl();
        HttpURLConnection connection = (HttpURLConnection) new URL(uploadUrl).openConnection();

        // Set up connection properties
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        // Add authentication and content headers
        Map<String, String> headers = this.connection.getMediaAuthHeaders();
        headers.forEach(connection::setRequestProperty);
        connection.setRequestProperty("Content-Type", "image/jpeg");
        connection.setRequestProperty("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        // Upload the image
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(imageBytes);
        }

        // Check response code
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_CREATED) {
            // Handle success response
            try (InputStream inputStream = connection.getInputStream()) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> responseJson = objectMapper.readValue(inputStream, Map.class);
                log.info("Upload successful. Image URL: {}", responseJson.get("source_url"));
                return String.valueOf(responseJson.get("source_url"));
            }
        } else {
            // Handle error response
            try (InputStream errorStream = connection.getErrorStream()) {
                if (errorStream != null) {
                    String errorResponse = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                    System.out.println("Error response: " + errorResponse);
                    throw new RuntimeException("Image upload failed. Response code: " + responseCode );
                }
            }
        }
        return "";

    }
    private String buildMediaUrl() {
        return AppConstants.BASE_URL + MEDIA_ENDPOINT;
    }
}


