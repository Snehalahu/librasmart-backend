package com.librasmart.service;

import com.librasmart.dto.AuthResponse;
import com.librasmart.dto.LoginRequest;
import com.librasmart.dto.RegisterRequest;
import com.librasmart.entity.Gamification;
import com.librasmart.entity.Student;
import com.librasmart.entity.User;
import com.librasmart.repository.GamificationRepository;
import com.librasmart.repository.StudentRepository;
import com.librasmart.repository.UserRepository;
import com.librasmart.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private GamificationRepository gamificationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        if (studentRepository.existsByStudentId(request.getStudentId())) {
            throw new RuntimeException("Student ID already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.STUDENT);
        user.setIsActive(true);
        User savedUser = userRepository.save(user);

        Student student = new Student();
        student.setUser(savedUser);
        student.setStudentId(request.getStudentId());
        student.setDepartment(request.getDepartment());
        student.setYear(request.getYear());
        student.setPhone(request.getPhone());
        studentRepository.save(student);

        Gamification gamification = new Gamification();
        gamification.setUser(savedUser);
        gamification.setTotalPoints(0);
        gamification.setCurrentStreak(0);
        gamification.setLongestStreak(0);
        gamification.setBooksRead(0);
        gamificationRepository.save(gamification);

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(savedUser.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(
                token,
                savedUser.getRole().name(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getId()
        );
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid email or password");
        }

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(
                token,
                user.getRole().name(),
                user.getName(),
                user.getEmail(),
                user.getId()
        );
    }
}