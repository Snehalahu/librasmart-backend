package com.librasmart.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.librasmart.entity.Book;
import com.librasmart.entity.BorrowRecord;
import com.librasmart.entity.User;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    List<BorrowRecord> findByUser(User user);
    List<BorrowRecord> findByBook(Book book);
    List<BorrowRecord> findByStatus(BorrowRecord.Status status);
    List<BorrowRecord> findByUserAndStatus(User user, BorrowRecord.Status status);
    Optional<BorrowRecord> findByUserAndBookAndStatus(User user, Book book, BorrowRecord.Status status);
    List<BorrowRecord> findByDueDateBeforeAndStatus(LocalDate date, BorrowRecord.Status status);
    List<BorrowRecord> findByBorrowDateBetween(LocalDate startDate, LocalDate endDate);
    Long countByUser(User user);
}