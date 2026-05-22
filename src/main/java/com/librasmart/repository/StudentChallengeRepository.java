package com.librasmart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.librasmart.entity.Challenge;
import com.librasmart.entity.StudentChallenge;
import com.librasmart.entity.User;

@Repository
public interface StudentChallengeRepository extends JpaRepository<StudentChallenge, Long> {
    List<StudentChallenge> findByUser(User user);
    List<StudentChallenge> findByChallenge(Challenge challenge);
    Optional<StudentChallenge> findByUserAndChallenge(User user, Challenge challenge);
    Boolean existsByUserAndChallenge(User user, Challenge challenge);
    List<StudentChallenge> findByUserAndStatus(User user, StudentChallenge.Status status);
}