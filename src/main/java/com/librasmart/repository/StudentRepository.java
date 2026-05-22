package com.librasmart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.librasmart.entity.Student;
import com.librasmart.entity.User;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUser(User user);
    Optional<Student> findByStudentId(String studentId);
    Boolean existsByStudentId(String studentId);
}