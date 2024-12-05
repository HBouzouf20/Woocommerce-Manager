package com.hbdev.woocommerce_manager.models;

import lombok.Builder;
import lombok.Getter;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a connection to a WooCommerce store.
 * Encapsulates base URL and authentication details for API calls.
 */
@Builder
public class WooCommerceConnection {

    @Getter
    private final String baseUrl;
    private final String consumerKey;
    private final String consumerSecret;

    /**
     * Constructor for WooCommerceConnection.
     *
     * @param baseUrl       The base URL of the WooCommerce store.
     * @param consumerKey   The WooCommerce consumer key for authentication.
     * @param consumerSecret The WooCommerce consumer secret for authentication.
     */
    public WooCommerceConnection(String baseUrl, String consumerKey, String consumerSecret) {
        this.baseUrl = baseUrl;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    /**
     * Builds the headers for WooCommerce API requests.
     *
     * @return A map of authentication headers.
     */
    public Map<String, String> getAuthHeaders() {
        // Construct the authentication header for Basic Authentication
        String auth = consumerKey + ":" + consumerSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        // Create and return the headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + encodedAuth);
        headers.put("Content-Type", "application/json");
        return headers;
    }
    /**
     * Builds the headers for WooCommerce API requests.
     *
     * @return A map of authentication headers.
     */
    public Map<String, String> getMediaAuthHeaders() {
        // Create and return the headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + Base64.getEncoder().encodeToString((consumerKey + ":" + consumerSecret).getBytes()));

        return headers;
    }

}
