package com.mediway.backend.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.mediway.backend.entity.Appointment;
import com.mediway.backend.entity.Doctor;
import com.mediway.backend.entity.Payment;
import com.mediway.backend.entity.Receipt;
import com.mediway.backend.entity.User;
import com.mediway.backend.repository.AppointmentRepository;
import com.mediway.backend.repository.DoctorRepository;
import com.mediway.backend.repository.PaymentRepository;
import com.mediway.backend.repository.ReceiptRepository;
import com.mediway.backend.repository.UserRepository;

@RestController
@RequestMapping("/payments")
public class ReceiptController {

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    // Generate receipt for a completed payment
    @PostMapping("/{paymentId}/generate-receipt")
    public ResponseEntity<?> generateReceipt(
            @PathVariable Long paymentId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            if (userId == null) {
                userId = 1L; // Default for testing
            }

            // Check if receipt already exists
            if (receiptRepository.existsByPaymentId(paymentId)) {
                Optional<Receipt> existingReceipt = receiptRepository.findByPaymentId(paymentId);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Receipt already exists",
                    "receipt", existingReceipt.get()
                ));
            }

            // Get payment details
            Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
            if (paymentOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Payment not found"
                ));
            }

            Payment payment = paymentOpt.get();
            
            // Verify payment belongs to user and is completed
            if (!payment.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", "Unauthorized access to payment"
                ));
            }

            if (!payment.getStatus().equals(Payment.Status.COMPLETED)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Cannot generate receipt for non-completed payment"
                ));
            }

            // Get related data
            Optional<User> userOpt = userRepository.findById(userId);
            Optional<Appointment> appointmentOpt = appointmentRepository.findById(payment.getAppointmentId());
            
            if (userOpt.isEmpty() || appointmentOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Related data not found"
                ));
            }

            User user = userOpt.get();
            Appointment appointment = appointmentOpt.get();
            
            // Get doctor details
            String doctorName = "Unknown Doctor";
            if (appointment.getDoctorId() != null) {
                Optional<Doctor> doctorOpt = doctorRepository.findById(appointment.getDoctorId());
                if (doctorOpt.isPresent()) {
                    doctorName = doctorOpt.get().getName();
                }
            }

            // Create receipt
            Receipt receipt = new Receipt();
            receipt.setPaymentId(paymentId);
            receipt.setUserId(userId);
            receipt.setAppointmentId(payment.getAppointmentId());
            receipt.setAmount(payment.getAmount());
            receipt.setPaymentMethod(payment.getPaymentMethod());
            receipt.setTransactionId(payment.getTransactionId());
            receipt.setPatientName(user.getName());
            receipt.setPatientEmail(user.getEmail());
            receipt.setDoctorName(doctorName);
            receipt.setServiceDescription("Medical Consultation - " + appointment.getStatus().toString());
            
            // Generate QR code for receipt
            String qrData = String.format("MEDIWAY-RECEIPT:%s:$%.2f:%s", 
                receipt.getReceiptNumber(), 
                receipt.getAmount().doubleValue(),
                user.getName());
            receipt.setQrCode(qrData);

            receipt = receiptRepository.save(receipt);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Receipt generated successfully",
                "receipt", receipt
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error generating receipt: " + e.getMessage()
            ));
        }
    }

    // Get receipt by payment ID
    @GetMapping("/receipt/payment/{paymentId}")
    public ResponseEntity<?> getReceiptByPayment(
            @PathVariable Long paymentId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            if (userId == null) {
                userId = 1L;
            }

            Optional<Receipt> receiptOpt = receiptRepository.findByPaymentId(paymentId);
            if (receiptOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Receipt receipt = receiptOpt.get();
            if (!receipt.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", "Unauthorized access to receipt"
                ));
            }

            return ResponseEntity.ok(receipt);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error fetching receipt: " + e.getMessage()
            ));
        }
    }

    // Get receipt by receipt number
    @GetMapping("/receipt/{receiptNumber}")
    public ResponseEntity<?> getReceiptByNumber(
            @PathVariable String receiptNumber,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            if (userId == null) {
                userId = 1L;
            }

            Optional<Receipt> receiptOpt = receiptRepository.findByReceiptNumber(receiptNumber);
            if (receiptOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Receipt receipt = receiptOpt.get();
            if (!receipt.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", "Unauthorized access to receipt"
                ));
            }

            return ResponseEntity.ok(receipt);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error fetching receipt: " + e.getMessage()
            ));
        }
    }

    // Get all receipts for user
    @GetMapping("/receipts/my-receipts")
    public ResponseEntity<?> getMyReceipts(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            if (userId == null) {
                userId = 1L;
            }

            System.out.println("[ReceiptController] getMyReceipts called with userId=" + userId);

            List<Receipt> receipts = receiptRepository.findByUserIdOrderByIssueDateDesc(userId);
            System.out.println("[ReceiptController] Receipts fetched: count=" + (receipts != null ? receipts.size() : 0));
            return ResponseEntity.ok(receipts);

        } catch (Exception e) {
            System.err.println("[ReceiptController] Error fetching receipts for userId=" + userId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error fetching receipts: " + e.getMessage()
            ));
        }
    }

    // Download receipt as PDF
    @GetMapping("/receipt/{receiptNumber}/pdf")
    public ResponseEntity<byte[]> downloadReceiptPDF(
            @PathVariable String receiptNumber,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            if (userId == null) {
                userId = 1L;
            }

            Optional<Receipt> receiptOpt = receiptRepository.findByReceiptNumber(receiptNumber);
            if (receiptOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Receipt receipt = receiptOpt.get();
            if (!receipt.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            byte[] pdfBytes = generatePDF(receipt);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "receipt-" + receiptNumber + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Generate PDF receipt
    private byte[] generatePDF(Receipt receipt) throws IOException, WriterException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Header
        document.add(new Paragraph("MEDIWAY HEALTH CENTER")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(20)
            .setBold());
        
        document.add(new Paragraph("Medical Payment Receipt")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(14)
            .setMarginBottom(20));

        // Receipt details table
        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 7}));
        table.setWidth(UnitValue.createPercentValue(100));

        // Add receipt information
        addTableRow(table, "Receipt Number:", receipt.getReceiptNumber());
        addTableRow(table, "Issue Date:", receipt.getIssueDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
        addTableRow(table, "Patient Name:", receipt.getPatientName());
        addTableRow(table, "Patient Email:", receipt.getPatientEmail());
        addTableRow(table, "Doctor:", receipt.getDoctorName());
        addTableRow(table, "Service:", receipt.getServiceDescription());
        addTableRow(table, "Amount:", "$" + receipt.getAmount().toString());
        addTableRow(table, "Payment Method:", receipt.getPaymentMethod());
        addTableRow(table, "Transaction ID:", receipt.getTransactionId());

        document.add(table);

        // Generate and add QR code
        if (receipt.getQrCode() != null) {
            byte[] qrCodeImage = generateQRCodeImage(receipt.getQrCode());
            Image qrImage = new Image(ImageDataFactory.create(qrCodeImage));
            qrImage.setWidth(100);
            qrImage.setHeight(100);
            
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Scan for verification:")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10));
            document.add(qrImage.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER));
        }

        // Footer
        document.add(new Paragraph("\nThank you for choosing MediWay Health Center!")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(10)
            .setMarginTop(20));
        
        document.add(new Paragraph("For any queries, please contact us at support@mediway.com")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(8));

        document.close();
        return baos.toByteArray();
    }

    private void addTableRow(Table table, String key, String value) {
        table.addCell(new Cell().add(new Paragraph(key).setBold()));
        table.addCell(new Cell().add(new Paragraph(value != null ? value : "N/A")));
    }

    private byte[] generateQRCodeImage(String text) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
        
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }
}