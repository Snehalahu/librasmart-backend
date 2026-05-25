package com.librasmart.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.librasmart.dto.BookDto;
import com.librasmart.entity.Book;
import com.librasmart.entity.Category;
import com.librasmart.repository.BookRepository;
import com.librasmart.repository.CategoryRepository;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public BookDto addBook(BookDto dto) {
        if (dto.getIsbn() != null && bookRepository.existsByIsbn(dto.getIsbn())) {
            throw new RuntimeException("Book with this ISBN already exists");
        }
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setCategory(category);
        book.setPublisher(dto.getPublisher());
        book.setPublishedYear(dto.getPublishedYear());
        book.setTotalCopies(dto.getTotalCopies());
        book.setAvailableCopies(dto.getTotalCopies());
        book.setDescription(dto.getDescription());
        book.setCoverImage(dto.getCoverImage());
        book.setEbookUrl(dto.getEbookUrl());

        return mapToDto(bookRepository.save(book));
    }

    public List<BookDto> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        return mapToDto(book);
    }

    public List<BookDto> searchBooks(String keyword) {
        return bookRepository
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
                        keyword, keyword)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> getBooksByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return bookRepository.findByCategory(category)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> getAvailableBooks() {
        return bookRepository.findByAvailableCopiesGreaterThan(0)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public BookDto updateBook(Long id, BookDto dto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setCategory(category);
        book.setPublisher(dto.getPublisher());
        book.setPublishedYear(dto.getPublishedYear());
        book.setTotalCopies(dto.getTotalCopies());
        book.setDescription(dto.getDescription());
        book.setCoverImage(dto.getCoverImage());
        book.setEbookUrl(dto.getEbookUrl());

        return mapToDto(bookRepository.save(book));
    }

    public void deleteBook(Long id) {
        bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        bookRepository.deleteById(id);
    }

    public BookDto mapToDto(Book book) {
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setPublisher(book.getPublisher());
        dto.setPublishedYear(book.getPublishedYear());
        dto.setTotalCopies(book.getTotalCopies());
        dto.setAvailableCopies(book.getAvailableCopies());
        dto.setDescription(book.getDescription());
        dto.setCoverImage(book.getCoverImage());
        dto.setEbookUrl(book.getEbookUrl());
        dto.setAverageRating(book.getAverageRating());
        if (book.getCategory() != null) {
            dto.setCategoryId(book.getCategory().getId());
            dto.setCategoryName(book.getCategory().getName());
        }
        return dto;
    }
}