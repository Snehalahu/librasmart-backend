package com.librasmart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.librasmart.entity.Gamification;
import com.librasmart.entity.User;

@Repository
public interface GamificationRepository extends JpaRepository<Gamification, Long> {
    Optional<Gamification> findByUser(User user);
    List<Gamification> findAllByOrderByTotalPointsDesc();
}