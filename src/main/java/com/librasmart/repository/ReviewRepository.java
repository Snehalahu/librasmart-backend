package com.librasmart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.librasmart.entity.Book;
import com.librasmart.entity.Review;
import com.librasmart.entity.User;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBook(Book book);
    List<Review> findByUser(User user);
    Optional<Review> findByUserAndBook(User user, Book book);
    Boolean existsByUserAndBook(User user, Book book);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book = :book")
    Double findAverageRatingByBook(Book book);
}