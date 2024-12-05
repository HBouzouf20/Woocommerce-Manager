package com.hbdev.woocommerce_manager.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbdev.woocommerce_manager.models.Product;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Stream;

/**
 * A helper class for recursively reading the contents of a folder.
 * This class provides methods to list files and directories in a functional style.
 */
public class FileHelper {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Recursively reads the contents of a folder.
     *
     * @param folderPath the path to the folder
     * @throws IOException if an I/O error occurs
     */
    public static void readFolder(String folderPath) throws IOException {
        Path startPath = Paths.get(folderPath);

        if (!Files.exists(startPath)) {
            throw new IllegalArgumentException("The folder does not exist: " + folderPath);
        }

        try (Stream<Path> paths = Files.walk(startPath)) {
            paths.forEach(path -> {
                if (Files.isDirectory(path)) {
                    System.out.println("Directory: " + path.toString());
                } else {
                    System.out.println("File: " + path.toString());
                }
            });
        }
    }

    /**
     * Recursively reads the contents of a folder and performs a custom action.
     *
     * @param folderPath the path to the folder
     * @param action     the action to perform on each file or folder
     * @throws IOException if an I/O error occurs
     */
    public static void readFolder(String folderPath, java.util.function.Consumer<Path> action) throws IOException {
        Path startPath = Paths.get(folderPath);

        if (!Files.exists(startPath)) {
            throw new IllegalArgumentException("The folder does not exist: " + folderPath);
        }

        try (Stream<Path> paths = Files.walk(startPath)) {
            paths.forEach(action);
        }
    }

    /**
     * Filters and lists files or directories matching a specific condition.
     *
     * @param folderPath the path to the folder
     * @param filter     a predicate to filter files or directories
     * @throws IOException if an I/O error occurs
     */
    public static void listFiltered(String folderPath, java.util.function.Predicate<Path> filter) throws IOException {
        Path startPath = Paths.get(folderPath);

        if (!Files.exists(startPath)) {
            throw new IllegalArgumentException("The folder does not exist: " + folderPath);
        }

        try (Stream<Path> paths = Files.walk(startPath)) {
            paths.filter(filter).forEach(path -> {
                System.out.println(path.toString());
            });
        }
    }
    /**
     * Reads product data from the specified folder and parses it into a list of products.
     *
     * @param folderPath The path to the folder containing product files.
     * @return A list of products parsed from the files.
     * @throws IOException If an error occurs while reading the files.
     */
    public static List<Product> readProductsFromFolder(String folderPath) throws IOException {
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null || files.length == 0) {
            return null;
        }

        List<Product> products = new java.util.ArrayList<>();
        for (File file : files) {
            Product product = objectMapper.readValue(file, Product.class);
            products.add(product);
        }
        return products;
    }
}
