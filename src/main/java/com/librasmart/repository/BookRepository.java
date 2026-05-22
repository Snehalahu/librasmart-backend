package com.librasmart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.librasmart.entity.Book;
import com.librasmart.entity.Category;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByAuthorContainingIgnoreCase(String author);
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);
    List<Book> findByCategory(Category category);
    Optional<Book> findByIsbn(String isbn);
    Boolean existsByIsbn(String isbn);
    List<Book> findByAvailableCopiesGreaterThan(Integer copies);
}