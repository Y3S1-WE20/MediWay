package com.mediway.backend.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;

import lombok.extern.slf4j.Slf4j;

/**
 * PayPal Configuration for Sandbox Integration
 * Configures PayPal REST API SDK with sandbox credentials
 */
@Configuration
@Slf4j
public class PayPalConfig {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    /**
     * Create PayPal API Context bean
     * This is used for all PayPal API interactions
     */
    @Bean
    public APIContext apiContext() {
        try {
            // Only create API context if we have valid credentials
            if (clientId == null || clientId.trim().isEmpty() ||
                clientSecret == null || clientSecret.trim().isEmpty()) {
                log.warn("PayPal credentials not configured, PayPal integration will use simulated mode");
                return null;
            }

            Map<String, String> configMap = new HashMap<>();
            configMap.put("mode", mode);

            APIContext context = new APIContext(clientId, clientSecret, mode);
            context.setConfigurationMap(configMap);

            log.info("PayPal API Context initialized in {} mode", mode);
            return context;
        } catch (Exception e) {
            log.error("Failed to initialize PayPal API Context: {}", e.getMessage());
            log.warn("PayPal integration will use simulated mode due to configuration error");
            return null;
        }
    }

    /**
     * Get PayPal access token
     * Useful for debugging and verification
     */
    public String getAccessToken() throws PayPalRESTException {
        Map<String, String> configMap = new HashMap<>();
        configMap.put("mode", mode);
        
        OAuthTokenCredential credential = new OAuthTokenCredential(clientId, clientSecret, configMap);
        return credential.getAccessToken();
    }
}
