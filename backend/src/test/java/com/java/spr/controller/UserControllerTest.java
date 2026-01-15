package com.java.spr.controller;

import com.java.spr.model.User;
import com.java.spr.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    // ================= DASHBOARD =================
    @Test
    void dashboard_shouldReturnDashboardMessage() {

        String response = userController.dashboard();

        assertThat(response).isEqualTo("USER dashboard access granted");
    }

    // ================= GET CURRENT USER =================
    @Test
    void getCurrentUser_shouldReturnUser() {

        when(authentication.getName())
                .thenReturn("user@test.com");

        User user = User.builder()
                .email("user@test.com")
                .active(true)
                .build();

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        User result = userController.getCurrentUser(authentication);

        assertThat(result.getEmail()).isEqualTo("user@test.com");
        assertThat(result.isActive()).isTrue();
    }

    // ================= USER NOT FOUND =================
    @Test
    void getCurrentUser_shouldFailWhenUserNotFound() {

        when(authentication.getName())
                .thenReturn("missing@test.com");

        when(userRepository.findByEmail("missing@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                userController.getCurrentUser(authentication)
        );
    }
}
