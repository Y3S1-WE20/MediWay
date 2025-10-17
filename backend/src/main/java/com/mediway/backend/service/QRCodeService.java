package com.mediway.backend.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import lombok.extern.slf4j.Slf4j;

/**
 * Service for generating and managing QR codes for patient identification
 */
@Service
@Slf4j
public class QRCodeService {

    private static final int QR_CODE_WIDTH = 300;
    private static final int QR_CODE_HEIGHT = 300;

    /**
     * Generate a unique QR code data string for a patient
     * @param userId The patient's user ID
     * @param email The patient's email
     * @return A unique QR code identifier string
     */
    public String generateQRCodeData(UUID userId, String email) {
        return String.format("MEDIWAY-PATIENT:%s:%s", userId.toString(), email);
    }

    /**
     * Generate QR code image as Base64 encoded string
     * @param qrCodeData The data to encode in the QR code
     * @return Base64 encoded PNG image of the QR code
     * @throws WriterException If QR code generation fails
     * @throws IOException If image conversion fails
     */
    public String generateQRCodeImage(String qrCodeData) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeData, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        byte[] qrCodeBytes = outputStream.toByteArray();

        return Base64.getEncoder().encodeToString(qrCodeBytes);
    }

    /**
     * Validate QR code data format
     * @param qrCodeData The QR code data to validate
     * @return true if the QR code format is valid
     */
    public boolean validateQRCodeData(String qrCodeData) {
        if (qrCodeData == null || qrCodeData.isEmpty()) {
            return false;
        }
        
        // Check if it starts with MEDIWAY-PATIENT prefix
        if (!qrCodeData.startsWith("MEDIWAY-PATIENT:")) {
            return false;
        }

        // Split and validate format: MEDIWAY-PATIENT:UUID:EMAIL
        String[] parts = qrCodeData.split(":");
        if (parts.length != 3) {
            return false;
        }

        try {
            // Validate UUID format by attempting to parse it
            UUID uuid = UUID.fromString(parts[1]);
            return uuid != null;
        } catch (IllegalArgumentException e) {
            log.warn("Invalid UUID in QR code data: {}", parts[1]);
            return false;
        }
    }

    /**
     * Extract user ID from QR code data
     * @param qrCodeData The QR code data
     * @return The extracted UUID
     */
    public UUID extractUserIdFromQRCode(String qrCodeData) {
        if (!validateQRCodeData(qrCodeData)) {
            throw new IllegalArgumentException("Invalid QR code format");
        }
        
        String[] parts = qrCodeData.split(":");
        return UUID.fromString(parts[1]);
    }
}
