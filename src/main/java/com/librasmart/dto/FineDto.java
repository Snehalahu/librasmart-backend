package com.librasmart.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class FineDto {
    private Long id;
    private Long userId;
    private String userName;
    private Long borrowRecordId;
    private String bookTitle;
    private BigDecimal amount;
    private Integer daysOverdue;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
}