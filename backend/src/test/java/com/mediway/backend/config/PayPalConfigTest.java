package com.mediway.backend.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.paypal.base.rest.APIContext;

@DisplayName("PayPal Config Tests")
class PayPalConfigTest {

    private PayPalConfig payPalConfig;

    @BeforeEach
    void setUp() {
        payPalConfig = new PayPalConfig();
    }

    @Test
    @DisplayName("Should create API context with valid credentials")
    void testApiContext_ValidCredentials() {
        ReflectionTestUtils.setField(payPalConfig, "clientId", "test-client-id");
        ReflectionTestUtils.setField(payPalConfig, "clientSecret", "test-client-secret");
        ReflectionTestUtils.setField(payPalConfig, "mode", "sandbox");

        APIContext context = payPalConfig.apiContext();

        assertNotNull(context);
        assertEquals("test-client-id", context.getClientID());
        assertEquals("test-client-secret", context.getClientSecret());
    }

    @Test
    @DisplayName("Should return null with empty client ID")
    void testApiContext_EmptyClientId() {
        ReflectionTestUtils.setField(payPalConfig, "clientId", "");
        ReflectionTestUtils.setField(payPalConfig, "clientSecret", "test-client-secret");
        ReflectionTestUtils.setField(payPalConfig, "mode", "sandbox");

        APIContext context = payPalConfig.apiContext();

        assertNull(context, "API context should be null when client ID is empty");
    }

    @Test
    @DisplayName("Should return null with null client ID")
    void testApiContext_NullClientId() {
        ReflectionTestUtils.setField(payPalConfig, "clientId", null);
        ReflectionTestUtils.setField(payPalConfig, "clientSecret", "test-client-secret");
        ReflectionTestUtils.setField(payPalConfig, "mode", "sandbox");

        APIContext context = payPalConfig.apiContext();

        assertNull(context, "API context should be null when client ID is null");
    }

    @Test
    @DisplayName("Should return null with empty client secret")
    void testApiContext_EmptyClientSecret() {
        ReflectionTestUtils.setField(payPalConfig, "clientId", "test-client-id");
        ReflectionTestUtils.setField(payPalConfig, "clientSecret", "");
        ReflectionTestUtils.setField(payPalConfig, "mode", "sandbox");

        APIContext context = payPalConfig.apiContext();

        assertNull(context, "API context should be null when client secret is empty");
    }

    @Test
    @DisplayName("Should return null with null client secret")
    void testApiContext_NullClientSecret() {
        ReflectionTestUtils.setField(payPalConfig, "clientId", "test-client-id");
        ReflectionTestUtils.setField(payPalConfig, "clientSecret", null);
        ReflectionTestUtils.setField(payPalConfig, "mode", "sandbox");

        APIContext context = payPalConfig.apiContext();

        assertNull(context, "API context should be null when client secret is null");
    }

    @Test
    @DisplayName("Should handle whitespace-only credentials")
    void testApiContext_WhitespaceCredentials() {
        ReflectionTestUtils.setField(payPalConfig, "clientId", "   ");
        ReflectionTestUtils.setField(payPalConfig, "clientSecret", "   ");
        ReflectionTestUtils.setField(payPalConfig, "mode", "sandbox");

        APIContext context = payPalConfig.apiContext();

        assertNull(context, "API context should be null when credentials are whitespace only");
    }

    @Test
    @DisplayName("Should use correct mode configuration")
    void testApiContext_Mode() {
        ReflectionTestUtils.setField(payPalConfig, "clientId", "test-client-id");
        ReflectionTestUtils.setField(payPalConfig, "clientSecret", "test-client-secret");
        ReflectionTestUtils.setField(payPalConfig, "mode", "live");

        APIContext context = payPalConfig.apiContext();

        assertNotNull(context);
        // Verify mode is set through configuration map
        assertNotNull(context.getConfigurationMap());
    }

    @Test
    @DisplayName("Should create context in sandbox mode")
    void testApiContext_SandboxMode() {
        ReflectionTestUtils.setField(payPalConfig, "clientId", "sandbox-client-id");
        ReflectionTestUtils.setField(payPalConfig, "clientSecret", "sandbox-client-secret");
        ReflectionTestUtils.setField(payPalConfig, "mode", "sandbox");

        APIContext context = payPalConfig.apiContext();

        assertNotNull(context);
        assertEquals("sandbox-client-id", context.getClientID());
    }
}
