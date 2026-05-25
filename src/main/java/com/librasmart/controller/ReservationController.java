package com.librasmart.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.librasmart.dto.ReservationDto;
import com.librasmart.service.ReservationService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping("/student/reserve/{bookId}/user/{userId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> createReservation(
            @PathVariable Long bookId,
            @PathVariable Long userId) {
        try {
            return ResponseEntity.ok(
                    reservationService.createReservation(userId, bookId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/student/reservations/{userId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ReservationDto>> getUserReservations(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                reservationService.getUserReservations(userId));
    }

    @GetMapping("/admin/reservations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReservationDto>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/admin/reservations/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReservationDto>> getPendingReservations() {
        return ResponseEntity.ok(
                reservationService.getPendingReservations());
    }

    @PutMapping("/admin/reservations/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            return ResponseEntity.ok(
                    reservationService.updateReservationStatus(id, status));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/student/reservations/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id) {
        try {
            reservationService.cancelReservation(id);
            return ResponseEntity.ok(
                    Map.of("message", "Reservation cancelled"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}