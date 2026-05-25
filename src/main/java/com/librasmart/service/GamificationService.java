package com.librasmart.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.librasmart.dto.GamificationDto;
import com.librasmart.entity.Badge;
import com.librasmart.entity.Gamification;
import com.librasmart.entity.User;
import com.librasmart.repository.BadgeRepository;
import com.librasmart.repository.GamificationRepository;
import com.librasmart.repository.UserRepository;

@Service
public class GamificationService {

    @Autowired
    private GamificationRepository gamificationRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserRepository userRepository;

    public GamificationDto getUserStats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Gamification g = gamificationRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException(
                        "Gamification record not found"));
        return mapToDto(g, null);
    }

    public List<GamificationDto> getLeaderboard() {
        List<Gamification> all = gamificationRepository
                .findAllByOrderByTotalPointsDesc();
        AtomicInteger rank = new AtomicInteger(1);
        return all.stream()
                .map(g -> mapToDto(g, rank.getAndIncrement()))
                .collect(Collectors.toList());
    }

    public void checkAndAwardBadges(User user) {
        Gamification g = gamificationRepository.findByUser(user)
                .orElse(null);
        if (g == null) return;

        if (g.getBooksRead() >= 1
                && !badgeRepository.existsByUserAndBadgeType(
                        user, Badge.BadgeType.BRONZE_READER)) {
            awardBadge(user, "Bronze Reader",
                    Badge.BadgeType.BRONZE_READER);
        }

        if (g.getBooksRead() >= 5
                && !badgeRepository.existsByUserAndBadgeType(
                        user, Badge.BadgeType.SILVER_SCHOLAR)) {
            awardBadge(user, "Silver Scholar",
                    Badge.BadgeType.SILVER_SCHOLAR);
        }

        if (g.getBooksRead() >= 10
                && !badgeRepository.existsByUserAndBadgeType(
                        user, Badge.BadgeType.GOLD_BOOKWORM)) {
            awardBadge(user, "Gold Bookworm",
                    Badge.BadgeType.GOLD_BOOKWORM);
        }
    }

    private void awardBadge(User user, String name,
            Badge.BadgeType type) {
        Badge badge = new Badge();
        badge.setUser(user);
        badge.setBadgeName(name);
        badge.setBadgeType(type);
        badgeRepository.save(badge);
    }

    public GamificationDto mapToDto(Gamification g, Integer rank) {
        GamificationDto dto = new GamificationDto();
        dto.setUserId(g.getUser().getId());
        dto.setUserName(g.getUser().getName());
        dto.setTotalPoints(g.getTotalPoints());
        dto.setCurrentStreak(g.getCurrentStreak());
        dto.setLongestStreak(g.getLongestStreak());
        dto.setBooksRead(g.getBooksRead());
        dto.setLastBorrowDate(g.getLastBorrowDate());
        dto.setRank(rank);
        List<String> badges = badgeRepository.findByUser(g.getUser())
                .stream()
                .map(Badge::getBadgeName)
                .collect(Collectors.toList());
        dto.setBadges(badges);
        return dto;
    }
}