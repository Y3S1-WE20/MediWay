package com.mediway.backend.service;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QRCodeServiceTest {

    /*
     * TESTS SUMMARY (QRCodeServiceTest):
     * - generateQRCodeData: valid input produces expected formatted string : Positive
     * - validateQRCodeData: handles valid and multiple invalid cases       : Positive/Negative
     * - extractUserIdFromQRCode: returns UUID or throws for invalid data    : Positive/Negative
     * - generateQRCodeImage: returns Base64 string for given data         : Positive
     */

    private final QRCodeService qrCodeService = new QRCodeService();

    @Test
    void generateQRCodeData_ValidInput_ReturnsFormattedString() {
        // Given
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";

        // When
        String result = qrCodeService.generateQRCodeData(userId, email);

        // Then
        assertNotNull(result);
        assertTrue(result.startsWith("MEDIWAY-PATIENT:"));
        assertTrue(result.contains(userId.toString()));
        assertTrue(result.contains(email));
    }

    @Test
    void validateQRCodeData_ValidData_ReturnsTrue() {
        // Given
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        String qrCodeData = qrCodeService.generateQRCodeData(userId, email);

        // When
        boolean result = qrCodeService.validateQRCodeData(qrCodeData);

        // Then
        assertTrue(result);
    }

    @Test
    void validateQRCodeData_InvalidData_ReturnsFalse() {
        // Test null
        assertFalse(qrCodeService.validateQRCodeData(null));

        // Test empty string
        assertFalse(qrCodeService.validateQRCodeData(""));

        // Test wrong prefix
        assertFalse(qrCodeService.validateQRCodeData("INVALID-PATIENT:123:test@example.com"));

        // Test invalid UUID
        assertFalse(qrCodeService.validateQRCodeData("MEDIWAY-PATIENT:invalid-uuid:test@example.com"));

        // Test wrong number of parts
        assertFalse(qrCodeService.validateQRCodeData("MEDIWAY-PATIENT:123"));
    }

    @Test
    void extractUserIdFromQRCode_ValidData_ReturnsUUID() {
        // Given
        UUID expectedUserId = UUID.randomUUID();
        String email = "test@example.com";
        String qrCodeData = qrCodeService.generateQRCodeData(expectedUserId, email);

        // When
        UUID result = qrCodeService.extractUserIdFromQRCode(qrCodeData);

        // Then
        assertEquals(expectedUserId, result);
    }

    @Test
    void extractUserIdFromQRCode_InvalidData_ThrowsException() {
        // Given
        String invalidQRCodeData = "INVALID-DATA";

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            qrCodeService.extractUserIdFromQRCode(invalidQRCodeData));
    }

    @Test
    void generateQRCodeImage_ValidData_ReturnsBase64String() throws Exception {
        // Given
        String qrCodeData = "MEDIWAY-PATIENT:123e4567-e89b-12d3-a456-426614174000:test@example.com";

        // When
        String result = qrCodeService.generateQRCodeImage(qrCodeData);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        // Should be valid Base64 and decode without throwing exception
        assertDoesNotThrow(() -> {
            byte[] decoded = java.util.Base64.getDecoder().decode(result);
            assertNotNull(decoded);
        });
    }
}