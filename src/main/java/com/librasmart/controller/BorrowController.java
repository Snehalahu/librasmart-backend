package com.librasmart.controller;

import com.librasmart.dto.BorrowDto;
import com.librasmart.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    // Student borrows a book
    @PostMapping("/student/borrow/{bookId}/user/{userId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> borrowBook(
            @PathVariable Long bookId,
            @PathVariable Long userId) {
        try {
            return ResponseEntity.ok(borrowService.borrowBook(userId, bookId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Student returns a book
    @PutMapping("/student/return/{borrowRecordId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> returnBook(
            @PathVariable Long borrowRecordId) {
        try {
            return ResponseEntity.ok(
                    borrowService.returnBook(borrowRecordId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Student gets their borrow history
    @GetMapping("/student/borrow/history/{userId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<BorrowDto>> getUserBorrowHistory(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                borrowService.getUserBorrowHistory(userId));
    }

    // Student gets their active borrows
    @GetMapping("/student/borrow/active/{userId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<BorrowDto>> getUserActiveBorrows(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                borrowService.getUserActiveBorrows(userId));
    }

    // Admin gets all borrow records
    @GetMapping("/admin/borrow/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BorrowDto>> getAllBorrowRecords() {
        return ResponseEntity.ok(borrowService.getAllBorrowRecords());
    }

    // Admin gets overdue books
    @GetMapping("/admin/borrow/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BorrowDto>> getOverdueBooks() {
        return ResponseEntity.ok(borrowService.getOverdueBooks());
    }
}