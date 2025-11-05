package com.fflog.staticmaker.util;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Component;

@Component
public class InputSanitizer {

    /**
     * Sanitize input to prevent XSS and other injection attacks
     */
    public String sanitize(String input) {
        if (input == null) {
            return null;
        }
        // Remove any HTML/XML tags and escape special characters
        return StringEscapeUtils.escapeHtml4(input.trim());
    }

    /**
     * Sanitize URL parameters for external API calls
     */
    public String sanitizeUrl(String input) {
        if (input == null) {
            return null;
        }
        // Remove dangerous characters that could be used for injection
        return input.replaceAll("[^a-zA-Z0-9\\-_]", "");
    }

    /**
     * Validate and sanitize server names for FFLogs API
     */
    public String sanitizeServerName(String server) {
        if (server == null || server.trim().isEmpty()) {
            return null;
        }
        // Only allow alphanumeric characters and hyphens
        String sanitized = server.trim().replaceAll("[^a-zA-Z\\-]", "");
        return sanitized.isEmpty() ? null : sanitized;
    }
}
