package com.java.spr.controller;

import com.java.spr.dto.CreateUserRequest;
import com.java.spr.dto.UpdateUserStatusRequest;
import com.java.spr.model.User;
import com.java.spr.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.java.spr.model.enums.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminController adminController;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // ================= CREATE USER =================
    @Test
    void createUser_shouldCreateNewUser() {

        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("new@test.com");
        request.setPassword("password");
        request.setRole(USER);

        when(userRepository.findByEmail("new@test.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("password"))
                .thenReturn("ENCODED");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User user = adminController.createUser(request);

        assertThat(user.getEmail()).isEqualTo("new@test.com");
        assertThat(user.isActive()).isTrue();
        assertThat(user.getCreatedAt()).isNotNull();
    }

    // ================= DUPLICATE EMAIL =================
    @Test
    void createUser_shouldFailWhenEmailExists() {

        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("existing@test.com");

        when(userRepository.findByEmail("existing@test.com"))
                .thenReturn(Optional.of(new User()));

        assertThrows(RuntimeException.class,
                () -> adminController.createUser(request));
    }

    // ================= UPDATE USER STATUS =================
    @Test
    void updateUserStatus_shouldUpdateUser() {

        User user = User.builder()
                .id("123")
                .email("user@test.com")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findById("123"))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // simulate logged-in admin
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "admin@test.com", null
                )
        );

        UpdateUserStatusRequest request = new UpdateUserStatusRequest();
        request.setActive(false);

        User updatedUser = adminController.updateUserStatus("123", request);

        assertThat(updatedUser.isActive()).isFalse();
    }

    // ================= PREVENT SELF DEACTIVATION =================
    @Test
    void updateUserStatus_shouldFailWhenAdminDeactivatesSelf() {

        User admin = User.builder()
                .id("999")
                .email("admin@test.com")
                .active(true)
                .build();

        when(userRepository.findById("999"))
                .thenReturn(Optional.of(admin));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "admin@test.com", null
                )
        );

        UpdateUserStatusRequest request = new UpdateUserStatusRequest();
        request.setActive(false);

        assertThrows(RuntimeException.class,
                () -> adminController.updateUserStatus("999", request));
    }
}
