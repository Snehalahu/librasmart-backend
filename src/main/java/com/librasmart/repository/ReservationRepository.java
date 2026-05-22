package com.librasmart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.librasmart.entity.Book;
import com.librasmart.entity.Reservation;
import com.librasmart.entity.User;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(User user);
    List<Reservation> findByBook(Book book);
    List<Reservation> findByStatus(Reservation.Status status);
    List<Reservation> findByUserAndStatus(User user, Reservation.Status status);
    Optional<Reservation> findByUserAndBookAndStatus(User user, Book book, Reservation.Status status);
}