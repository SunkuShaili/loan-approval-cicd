package com.java.spr.controller;

import com.java.spr.dto.CreateUserRequest;
import com.java.spr.dto.UpdateUserStatusRequest;
import com.java.spr.model.User;
import com.java.spr.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard() {
        return "ADMIN dashboard access granted";
    }



    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    @PostMapping
    public User createUser(@RequestBody CreateUserRequest request) {


        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }


    @PutMapping("/{id}/status")
    public User updateUserStatus(
            @PathVariable String id,
            @RequestBody UpdateUserStatusRequest request
    ) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));


        String loggedInEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        //Prevent admin from deactivating self
        if (!request.getActive() && user.getEmail().equals(loggedInEmail)) {
            throw new RuntimeException("Admin cannot deactivate self");
        }

        user.setActive(request.getActive());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }


}










