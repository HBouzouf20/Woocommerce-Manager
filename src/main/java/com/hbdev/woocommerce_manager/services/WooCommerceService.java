package com.hbdev.woocommerce_manager.services;

import com.hbdev.woocommerce_manager.models.WooCommerceConnection;

/**
 * Interface for WooCommerceService providing CRUD operations.
 *
 * @param <T> The type of resource to manage (e.g., Product, Order).
 */
public interface WooCommerceService<T> {
    void connection(WooCommerceConnection connection);

    T create(T resource) throws Exception;

    T read(String id) throws Exception;

    T update(String id, T resource) throws Exception;

    boolean delete(String id) throws Exception;


    void uploadImage(byte[] imageBytes, String fileName) throws Exception;
}
