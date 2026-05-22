package com.librasmart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.librasmart.entity.Badge;
import com.librasmart.entity.User;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
    List<Badge> findByUser(User user);
    Boolean existsByUserAndBadgeType(User user, Badge.BadgeType badgeType);
}