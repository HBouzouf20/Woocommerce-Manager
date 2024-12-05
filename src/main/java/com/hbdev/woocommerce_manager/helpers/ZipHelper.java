package com.hbdev.woocommerce_manager.helpers;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.*;

@Slf4j
public class ZipHelper {

    // Method to list all files in a ZIP archive
    public static void listFiles(String zipFilePath) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;
            System.out.println("Files in ZIP:");
            while ((entry = zipInputStream.getNextEntry()) != null) {
                System.out.println("- " + entry.getName());
                zipInputStream.closeEntry();
            }
        }
    }

    /**
     * Extracts all files from a ZIP archive to the specified output directory.
     *
     * @param zipFilePath the path to the ZIP file.
     * @param outputDir   the directory where the extracted files will be placed.
     * @throws IOException if an I/O error occurs during extraction.
     */
    public static void extractAll(String zipFilePath, String outputDir) throws IOException {
        Path outputPath = Paths.get(outputDir);
        Files.createDirectories(outputPath); // Ensure output directory exists

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;

            while ((entry = zipInputStream.getNextEntry()) != null) {
                // Resolve the file path for the current entry
                Path filePath = outputPath.resolve(entry.getName()).normalize();

                // Validate file path to prevent directory traversal attacks
                if (!filePath.startsWith(outputPath)) {
                    throw new IOException("Entry is outside of the target dir: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(filePath); // Create directory if entry is a directory
                } else {
                    Files.createDirectories(filePath.getParent()); // Ensure parent directories exist
                    try (OutputStream outputStream = Files.newOutputStream(filePath)) {
                        copyData(zipInputStream, outputStream); // Copy data to the file
                    }
                }

                zipInputStream.closeEntry(); // Close the current entry
            }
        }
        log.info("All files extracted in {}", outputDir );
    }

    /**
     * Copies data from the input stream to the output stream.
     *
     * @param input  the input stream.
     * @param output the output stream.
     * @throws IOException if an I/O error occurs during copying.
     */
    private static void copyData(ZipInputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) > 0) {
            output.write(buffer, 0, bytesRead);
        }
    }
    /**
     * Reads files from a ZIP archive without extracting them.
     *
     * @param zipFilePath the path to the ZIP file.
     * @return a map containing file names as keys and their contents as byte arrays.
     * @throws IOException if an I/O error occurs during reading.
     */
    public static Map<String, byte[]> readFilesFromZip(String zipFilePath) throws IOException {
        Map<String, byte[]> files = new HashMap<>();

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;

            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    files.put(entry.getName(), readEntryData(zipInputStream));
                }
                zipInputStream.closeEntry();
            }
        }

        return files;
    }

    /**
     * Reads the data of a single entry in the ZIP file.
     *
     * @param inputStream the ZIP input stream.
     * @return the data of the entry as a byte array.
     * @throws IOException if an I/O error occurs during reading.
     */
    private static byte[] readEntryData(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, bytesRead);
        }

        return outputStream.toByteArray();
    }

    /**
     * Converts a byte array to a String using UTF-8 encoding.
     *
     * @param data the byte array.
     * @return the string representation of the data.
     */
    public static String bytesToString(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }

    // Method to read the content of a specific file in the ZIP archive
    public static String readFile(String zipFilePath, String fileName) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().equals(fileName)) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = zipInputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                    return outputStream.toString();
                }
                zipInputStream.closeEntry();
            }
        }
        throw new FileNotFoundException("File " + fileName + " not found in the ZIP archive.");
    }
}
