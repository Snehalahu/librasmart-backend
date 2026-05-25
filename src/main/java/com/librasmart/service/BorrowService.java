package com.librasmart.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.librasmart.dto.BorrowDto;
import com.librasmart.entity.Book;
import com.librasmart.entity.BorrowRecord;
import com.librasmart.entity.Fine;
import com.librasmart.entity.Notification;
import com.librasmart.entity.User;
import com.librasmart.repository.BookRepository;
import com.librasmart.repository.BorrowRecordRepository;
import com.librasmart.repository.FineRepository;
import com.librasmart.repository.GamificationRepository;
import com.librasmart.repository.NotificationRepository;
import com.librasmart.repository.UserRepository;

@Service
public class BorrowService {

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private GamificationRepository gamificationRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    @Lazy
    private GamificationService gamificationService;

    // Borrow a book
    @Transactional
    public BorrowDto borrowBook(Long userId, Long bookId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        // Check if book is available
        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException(
                    "No copies available for this book");
        }

        // Check if user already borrowed this book
        borrowRecordRepository.findByUserAndBookAndStatus(
                user, book, BorrowRecord.Status.BORROWED)
                .ifPresent(b -> {
                    throw new RuntimeException(
                            "You have already borrowed this book");
                });

        // Create borrow record
        BorrowRecord record = new BorrowRecord();
        record.setUser(user);
        record.setBook(book);
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusDays(14));
        record.setStatus(BorrowRecord.Status.BORROWED);
        record.setFinePerDay(new BigDecimal("5.00"));
        BorrowRecord saved = borrowRecordRepository.save(record);

        // Decrease available copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        // Add points for borrowing
        addPoints(user, 10);

        // Send notification
        sendNotification(user, "Book Borrowed",
                "You borrowed '" + book.getTitle()
                        + "'. Due date: " + record.getDueDate(),
                Notification.Type.DUE_DATE);

        return mapToDto(saved);
    }

    // Return a book
    @Transactional
    public BorrowDto returnBook(Long borrowRecordId) {

        BorrowRecord record = borrowRecordRepository
                .findById(borrowRecordId)
                .orElseThrow(() -> new RuntimeException(
                        "Borrow record not found"));

        if (record.getStatus() == BorrowRecord.Status.RETURNED) {
            throw new RuntimeException("Book already returned");
        }

        // Set return date and status
        record.setReturnDate(LocalDate.now());
        record.setStatus(BorrowRecord.Status.RETURNED);
        borrowRecordRepository.save(record);

        // Increase available copies
        Book book = record.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        // Calculate fine if overdue
        BigDecimal fine = BigDecimal.ZERO;
        if (LocalDate.now().isAfter(record.getDueDate())) {
            long daysOverdue = ChronoUnit.DAYS.between(
                    record.getDueDate(), LocalDate.now());
            fine = record.getFinePerDay()
                    .multiply(new BigDecimal(daysOverdue));

            // Save fine record
            Fine fineRecord = new Fine();
            fineRecord.setBorrowRecord(record);
            fineRecord.setUser(record.getUser());
            fineRecord.setAmount(fine);
            fineRecord.setDaysOverdue((int) daysOverdue);
            fineRecord.setStatus(Fine.Status.PENDING);
            fineRepository.save(fineRecord);

            // Deduct points for late return
            addPoints(record.getUser(), -10);

            // Send fine notification
            sendNotification(record.getUser(), "Fine Applied",
                    "You have a fine of Rs." + fine
                            + " for late return of '"
                            + book.getTitle() + "'",
                    Notification.Type.FINE);
        } else {
            // On time return — add points
            addPoints(record.getUser(), 20);
        }

        // Update gamification books read
        updateBooksRead(record.getUser());

        // Check and award badges
        gamificationService.checkAndAwardBadges(record.getUser());

        BorrowDto dto = mapToDto(record);
        dto.setTotalFine(fine);
        return dto;
    }

    // Get all borrow records for a user
    public List<BorrowDto> getUserBorrowHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return borrowRecordRepository.findByUser(user)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Get all active borrows for a user
    public List<BorrowDto> getUserActiveBorrows(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return borrowRecordRepository
                .findByUserAndStatus(user, BorrowRecord.Status.BORROWED)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Get all borrow records — Admin
    public List<BorrowDto> getAllBorrowRecords() {
        return borrowRecordRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Get overdue books — Admin
    public List<BorrowDto> getOverdueBooks() {
        return borrowRecordRepository
                .findByDueDateBeforeAndStatus(
                        LocalDate.now(), BorrowRecord.Status.BORROWED)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Helper — add points
    private void addPoints(User user, int points) {
        gamificationRepository.findByUser(user).ifPresent(g -> {
            g.setTotalPoints(Math.max(0, g.getTotalPoints() + points));
            gamificationRepository.save(g);
        });
    }

    // Helper — update books read count
    private void updateBooksRead(User user) {
        gamificationRepository.findByUser(user).ifPresent(g -> {
            g.setBooksRead(g.getBooksRead() + 1);
            g.setLastBorrowDate(LocalDate.now());
            gamificationRepository.save(g);
        });
    }

    // Helper — send notification
    private void sendNotification(User user, String title,
            String message, Notification.Type type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setIsRead(false);
        notificationRepository.save(notification);
    }

    // Map entity to DTO
    public BorrowDto mapToDto(BorrowRecord record) {
        BorrowDto dto = new BorrowDto();
        dto.setId(record.getId());
        dto.setUserId(record.getUser().getId());
        dto.setUserName(record.getUser().getName());
        dto.setBookId(record.getBook().getId());
        dto.setBookTitle(record.getBook().getTitle());
        dto.setBookAuthor(record.getBook().getAuthor());
        dto.setBorrowDate(record.getBorrowDate());
        dto.setDueDate(record.getDueDate());
        dto.setReturnDate(record.getReturnDate());
        dto.setStatus(record.getStatus().name());
        dto.setFinePerDay(record.getFinePerDay());
        return dto;
    }
}