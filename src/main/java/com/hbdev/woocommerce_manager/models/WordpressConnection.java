package com.hbdev.woocommerce_manager.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WordpressConnection {
    private String username;
    private String password;

    public Map<String, String> getMediaAuthHeaders() {
        String username = getUsername();
        String applicationPassword = getPassword(); // Generated in WordPress

        // Combine username and application password in "username:password" format
        String authString = username + ":" + applicationPassword;

        // Encode the authentication string in Base64
        String authHeaderValue = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes());

        // Create headers map
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", authHeaderValue);

        return headers;
    }
}
