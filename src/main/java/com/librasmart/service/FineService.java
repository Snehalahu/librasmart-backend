package com.librasmart.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.librasmart.dto.FineDto;
import com.librasmart.entity.Fine;
import com.librasmart.entity.User;
import com.librasmart.repository.FineRepository;
import com.librasmart.repository.UserRepository;

@Service
public class FineService {

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private UserRepository userRepository;

    public List<FineDto> getUserFines(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return fineRepository.findByUser(user)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<FineDto> getUserPendingFines(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return fineRepository.findByUserAndStatus(user, Fine.Status.PENDING)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<FineDto> getAllFines() {
        return fineRepository.findAll()
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<FineDto> getAllPendingFines() {
        return fineRepository.findByStatus(Fine.Status.PENDING)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public FineDto payFine(Long fineId) {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new RuntimeException("Fine not found"));
        if (fine.getStatus() == Fine.Status.PAID) {
            throw new RuntimeException("Fine already paid");
        }
        fine.setStatus(Fine.Status.PAID);
        fine.setPaidAt(LocalDateTime.now());
        return mapToDto(fineRepository.save(fine));
    }

    public FineDto mapToDto(Fine fine) {
        FineDto dto = new FineDto();
        dto.setId(fine.getId());
        dto.setUserId(fine.getUser().getId());
        dto.setUserName(fine.getUser().getName());
        dto.setBorrowRecordId(fine.getBorrowRecord().getId());
        dto.setBookTitle(fine.getBorrowRecord().getBook().getTitle());
        dto.setAmount(fine.getAmount());
        dto.setDaysOverdue(fine.getDaysOverdue());
        dto.setStatus(fine.getStatus().name());
        dto.setCreatedAt(fine.getCreatedAt());
        dto.setPaidAt(fine.getPaidAt());
        return dto;
    }
}