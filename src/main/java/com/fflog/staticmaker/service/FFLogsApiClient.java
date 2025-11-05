package com.fflog.staticmaker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fflog.staticmaker.model.RegionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * FFLogs API v2 Client using GraphQL and OAuth 2.0
 * Documentation: https://www.fflogs.com/api/docs
 */
@Service
@Slf4j
public class FFLogsApiClient {

    private static final String TOKEN_URL = "https://www.fflogs.com/oauth/token";
    private static final String API_URL = "https://www.fflogs.com/api/v2/client";

    @Value("${fflogs.client.id:#{null}}")
    private String clientId;

    @Value("${fflogs.client.secret:#{null}}")
    private String clientSecret;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private String cachedAccessToken;
    private long tokenExpiryTime;

    public FFLogsApiClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Check if a character exists on FFLogs using the proper GraphQL API
     *
     * @param firstname Character first name
     * @param surname   Character surname
     * @param server    Server name
     * @param region    Region type
     * @return true if character does NOT exist, false if character exists
     */
    public boolean characterDoesNotExist(String firstname, String surname, String server, RegionType region) {
        // If API credentials are not configured, fall back to always allowing
        if (clientId == null || clientSecret == null || clientId.isEmpty() || clientSecret.isEmpty()) {
            log.warn("FFLogs API credentials not configured. Skipping character validation.");
            return false; // Allow character creation
        }

        try {
            String accessToken = getAccessToken();
            String fullName = firstname + " " + surname;
            String regionCode = region.getFflogsCode();

            // GraphQL query to check if character exists
            String query = String.format("""
                    {
                      characterData {
                        character(name: "%s", serverSlug: "%s", serverRegion: "%s") {
                          id
                          name
                        }
                      }
                    }
                    """, fullName, server, regionCode);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("query", query);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    API_URL,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode characterNode = root.path("data").path("characterData").path("character");

                // If character node is null or missing, character doesn't exist
                if (characterNode.isMissingNode() || characterNode.isNull()) {
                    log.debug("Character not found: {} {} on {} ({})", firstname, surname, server, regionCode);
                    return true; // Character does NOT exist
                }

                // Character exists
                log.debug("Character found: {} {} on {} ({})", firstname, surname, server, regionCode);
                return false; // Character exists
            }

            log.error("Unexpected response from FFLogs API: {}", response.getStatusCode());
            return false; // Fail open - allow character creation if API is down

        } catch (Exception e) {
            log.error("Error checking character on FFLogs: {}", e.getMessage());
            return false; // Fail open - allow character creation if API error
        }
    }

    /**
     * Get OAuth 2.0 access token using client credentials flow
     */
    private String getAccessToken() throws Exception {
        // Check if we have a valid cached token
        if (cachedAccessToken != null && System.currentTimeMillis() < tokenExpiryTime) {
            return cachedAccessToken;
        }

        // Request new token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Basic Authentication: Base64(client_id:client_secret)
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);

        String body = "grant_type=client_credentials";

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                TOKEN_URL,
                HttpMethod.POST,
                request,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            JsonNode root = objectMapper.readTree(response.getBody());
            cachedAccessToken = root.path("access_token").asText();
            int expiresIn = root.path("expires_in").asInt(3600); // Default 1 hour

            // Set expiry time (with 5 minute buffer)
            tokenExpiryTime = System.currentTimeMillis() + ((expiresIn - 300) * 1000L);

            log.debug("Obtained new FFLogs API access token (expires in {} seconds)", expiresIn);
            return cachedAccessToken;
        }

        throw new Exception("Failed to obtain FFLogs API access token");
    }

    /**
     * Test the API connection and credentials
     */
    public boolean testConnection() {
        try {
            String token = getAccessToken();
            return token != null && !token.isEmpty();
        } catch (Exception e) {
            log.error("FFLogs API connection test failed: {}", e.getMessage());
            return false;
        }
    }
}
