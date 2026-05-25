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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.librasmart.dto.GamificationDto;
import com.librasmart.service.GamificationService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class GamificationController {

    @Autowired
    private GamificationService gamificationService;

    @GetMapping("/student/gamification/{userId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getUserStats(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(
                    gamificationService.getUserStats(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<GamificationDto>> getLeaderboard() {
        return ResponseEntity.ok(gamificationService.getLeaderboard());
    }
}