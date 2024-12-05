package com.hbdev.woocommerce_manager.helpers;

public class Tools {
    public static String urlFormat(String url) {
        // Replace backslashes, forward slashes, and other unwanted characters with an underscore (_)
        String formattedUrl = url.replaceAll("[/\\\\]", "_");  // Replace / and \ with _
        formattedUrl = formattedUrl.replaceAll("[^a-zA-Z0-9_]", "_");  // Replace non-alphanumeric characters with _

        return formattedUrl;
    }

}
