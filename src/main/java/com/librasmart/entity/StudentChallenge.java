package com.librasmart.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "student_challenges",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"user_id", "challenge_id"}
    )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @Column(name = "books_completed")
    private Integer booksCompleted = 0;

    @Enumerated(EnumType.STRING)
    private Status status = Status.IN_PROGRESS;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
    }

    public enum Status {
        IN_PROGRESS, COMPLETED, FAILED
    }
}