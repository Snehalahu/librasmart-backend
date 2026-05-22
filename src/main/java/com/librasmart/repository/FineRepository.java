package com.librasmart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.librasmart.entity.BorrowRecord;
import com.librasmart.entity.Fine;
import com.librasmart.entity.User;

@Repository
public interface FineRepository extends JpaRepository<Fine, Long> {
    List<Fine> findByUser(User user);
    List<Fine> findByStatus(Fine.Status status);
    List<Fine> findByUserAndStatus(User user, Fine.Status status);
    Optional<Fine> findByBorrowRecord(BorrowRecord borrowRecord);
}