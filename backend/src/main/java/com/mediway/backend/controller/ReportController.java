package com.mediway.backend.controller;

import com.mediway.backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/admin/reports")
public class ReportController {

    @Autowired
    private AdminService adminService;

    @GetMapping(value = "/summary/csv", produces = "text/csv")
    public ResponseEntity<InputStreamResource> downloadCsvReport() {
        byte[] csv = adminService.generateCsvReport();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(new ByteArrayInputStream(csv)));
    }

    @GetMapping(value = "/summary/pdf", produces = "application/pdf")
    public ResponseEntity<InputStreamResource> downloadPdfReport() {
        byte[] pdf = adminService.generatePdfReport();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(new ByteArrayInputStream(pdf)));
    }
}
