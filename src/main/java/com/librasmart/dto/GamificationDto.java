package com.librasmart.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class GamificationDto {
    private Long userId;
    private String userName;
    private Integer totalPoints;
    private Integer currentStreak;
    private Integer longestStreak;
    private Integer booksRead;
    private LocalDate lastBorrowDate;
    private List<String> badges;
    private Integer rank;
}