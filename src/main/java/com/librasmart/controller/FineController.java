package com.librasmart.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.librasmart.dto.FineDto;
import com.librasmart.service.FineService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class FineController {

    @Autowired
    private FineService fineService;

    @GetMapping("/student/fines/{userId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<FineDto>> getUserFines(
            @PathVariable Long userId) {
        return ResponseEntity.ok(fineService.getUserFines(userId));
    }

    @GetMapping("/student/fines/{userId}/pending")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<FineDto>> getUserPendingFines(
            @PathVariable Long userId) {
        return ResponseEntity.ok(fineService.getUserPendingFines(userId));
    }

    @GetMapping("/admin/fines")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FineDto>> getAllFines() {
        return ResponseEntity.ok(fineService.getAllFines());
    }

    @GetMapping("/admin/fines/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FineDto>> getAllPendingFines() {
        return ResponseEntity.ok(fineService.getAllPendingFines());
    }

    @PutMapping("/admin/fines/{id}/pay")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> payFine(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(fineService.payFine(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}