package com.librasmart.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.librasmart.dto.ReservationDto;
import com.librasmart.entity.Book;
import com.librasmart.entity.Notification;
import com.librasmart.entity.Reservation;
import com.librasmart.entity.User;
import com.librasmart.repository.BookRepository;
import com.librasmart.repository.NotificationRepository;
import com.librasmart.repository.ReservationRepository;
import com.librasmart.repository.UserRepository;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Transactional
    public ReservationDto createReservation(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        reservationRepository.findByUserAndBookAndStatus(
                user, book, Reservation.Status.PENDING)
                .ifPresent(r -> {
                    throw new RuntimeException(
                            "You already have a pending reservation for this book");
                });

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setStatus(Reservation.Status.PENDING);
        reservation.setExpiryDate(LocalDate.now().plusDays(7));
        Reservation saved = reservationRepository.save(reservation);

        sendNotification(user, "Reservation Created",
                "Your reservation for '" + book.getTitle()
                        + "' has been created successfully.",
                Notification.Type.RESERVATION);

        return mapToDto(saved);
    }

    public List<ReservationDto> getUserReservations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return reservationRepository.findByUser(user)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<ReservationDto> getAllReservations() {
        return reservationRepository.findAll()
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<ReservationDto> getPendingReservations() {
        return reservationRepository.findByStatus(Reservation.Status.PENDING)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public ReservationDto updateReservationStatus(Long id, String status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Reservation not found"));
        Reservation.Status newStatus = Reservation.Status.valueOf(
                status.toUpperCase());
        reservation.setStatus(newStatus);
        Reservation updated = reservationRepository.save(reservation);

        String message = newStatus == Reservation.Status.APPROVED
                ? "Your reservation for '" + reservation.getBook().getTitle()
                        + "' has been approved!"
                : "Your reservation for '" + reservation.getBook().getTitle()
                        + "' has been " + status.toLowerCase() + ".";

        sendNotification(reservation.getUser(), "Reservation Update",
                message, Notification.Type.RESERVATION);

        return mapToDto(updated);
    }

    @Transactional
    public void cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Reservation not found"));
        reservation.setStatus(Reservation.Status.CANCELLED);
        reservationRepository.save(reservation);
    }

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

    public ReservationDto mapToDto(Reservation r) {
        ReservationDto dto = new ReservationDto();
        dto.setId(r.getId());
        dto.setUserId(r.getUser().getId());
        dto.setUserName(r.getUser().getName());
        dto.setBookId(r.getBook().getId());
        dto.setBookTitle(r.getBook().getTitle());
        dto.setBookAuthor(r.getBook().getAuthor());
        dto.setReservationDate(r.getReservationDate());
        dto.setExpiryDate(r.getExpiryDate());
        dto.setStatus(r.getStatus().name());
        return dto;
    }
}