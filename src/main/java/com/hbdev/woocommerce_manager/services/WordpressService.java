package com.hbdev.woocommerce_manager.services;

import com.hbdev.woocommerce_manager.models.WordpressConnection;

public interface WordpressService {
    void connection (WordpressConnection connection);

    String uploadImage(byte[] imageBytes, String fileName) throws Exception;
}
