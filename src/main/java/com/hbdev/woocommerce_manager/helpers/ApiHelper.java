package com.hbdev.woocommerce_manager.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.io.OutputStream;

/**
 * Helper class for making HTTP requests to the WooCommerce API.
 * This class handles the serialization and deserialization of generic types.
 *
 * @param <T> The type of object to handle (e.g., Product, Order).
 */
@Slf4j
public class ApiHelper<T> {

    private final ObjectMapper objectMapper;

    public ApiHelper() {
        this.objectMapper = new ObjectMapper();  // JSON processing
    }

    /**
     * Perform a POST request to the WooCommerce API.
     *
     * @param url      The URL to send the request to.
     * @param headers  The HTTP headers to include in the request.
     * @param body     The body of the POST request (of type T).
     * @return         The response body mapped to type T.
     * @throws IOException If an I/O error occurs.
     */
    public T post(String url, Map<String, String> headers, T body) throws IOException {
        String requestBody = objectMapper.writeValueAsString(body);
        System.out.println("requestBody = " + requestBody);
        String response = executePost(url, headers, requestBody);
        return objectMapper.readValue(response, new TypeReference<T>() {});
    }

    /**
     * Perform a GET request to the WooCommerce API.
     *
     * @param url      The URL to send the request to.
     * @param headers  The HTTP headers to include in the request.
     * @return         The response body mapped to type T.
     * @throws IOException If an I/O error occurs.
     */
    public T get(String url, Map<String, String> headers) throws IOException {
        String response = executeGet(url, headers);
        return objectMapper.readValue(response, new TypeReference<T>() {});
    }

    /**
     * Perform a PUT request to the WooCommerce API.
     *
     * @param url      The URL to send the request to.
     * @param headers  The HTTP headers to include in the request.
     * @param body     The body of the PUT request (of type T).
     * @return         The response body mapped to type T.
     * @throws IOException If an I/O error occurs.
     */
    public T put(String url, Map<String, String> headers, T body) throws IOException {
        String requestBody = objectMapper.writeValueAsString(body);
        String response = executePut(url, headers, requestBody);
        return objectMapper.readValue(response, new TypeReference<T>() {});
    }

    /**
     * Perform a DELETE request to the WooCommerce API.
     *
     * @param url      The URL to send the request to.
     * @param headers  The HTTP headers to include in the request.
     * @throws IOException If an I/O error occurs.
     */
    public void delete(String url, Map<String, String> headers) throws IOException {
        executeDelete(url, headers);
    }

    /**
     * Execute a POST request to the given URL.
     *
     * @param url        The URL to send the request to.
     * @param headers    The HTTP headers to include in the request.
     * @param body       The body to send with the request.
     * @return           The response body as a string.
     * @throws IOException If an I/O error occurs.
     */
    private String executePost(String url, Map<String, String> headers, String body) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = body.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        return readResponse(connection);
    }

    /**
     * Execute a GET request to the given URL.
     *
     * @param url        The URL to send the request to.
     * @param headers    The HTTP headers to include in the request.
     * @return           The response body as a string.
     * @throws IOException If an I/O error occurs.
     */
    private String executeGet(String url, Map<String, String> headers) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }

        return readResponse(connection);
    }

    /**
     * Execute a PUT request to the given URL.
     *
     * @param url        The URL to send the request to.
     * @param headers    The HTTP headers to include in the request.
     * @param body       The body to send with the request.
     * @return           The response body as a string.
     * @throws IOException If an I/O error occurs.
     */
    private String executePut(String url, Map<String, String> headers, String body) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = body.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        return readResponse(connection);
    }

    /**
     * Execute a DELETE request to the given URL.
     *
     * @param url        The URL to send the request to.
     * @param headers    The HTTP headers to include in the request.
     * @throws IOException If an I/O error occurs.
     */
    private void executeDelete(String url, Map<String, String> headers) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("DELETE");

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }

        readResponse(connection); // Read the response, even if it's empty
    }

    /**
     * Reads the response from the HTTP connection.
     *
     * @param connection The HTTP connection.
     * @return The response body as a string.
     * @throws IOException If an I/O error occurs.
     */
    private String readResponse(HttpURLConnection connection) throws IOException {
        int status = connection.getResponseCode();
        if (status >= 200 && status < 300) {
            return new String(connection.getInputStream().readAllBytes(), "UTF-8");
        } else {
            throw new IOException("HTTP request failed with status: " + status);
        }
    }

}
