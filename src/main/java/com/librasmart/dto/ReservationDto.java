package com.librasmart.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ReservationDto {
    private Long id;
    private Long userId;
    private String userName;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private LocalDateTime reservationDate;
    private LocalDate expiryDate;
    private String status;
}