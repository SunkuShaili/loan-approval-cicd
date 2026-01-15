package com.java.spr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.spr.dto.LoginRequest;
import com.java.spr.dto.LoginResponse;
import com.java.spr.security.JwtUtil;
import com.java.spr.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    // ================= LOGIN SUCCESS =================
    @Test
    void shouldLoginSuccessfully() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("password123");

        UserDetails userDetails = User.withUsername("user@test.com")
                .password("encoded-password")
                .authorities("ROLE_USER")
                .build();

        when(userDetailsService.loadUserByUsername("user@test.com"))
                .thenReturn(userDetails);

        when(passwordEncoder.matches("password123", "encoded-password"))
                .thenReturn(true);

        when(jwtUtil.generateToken("user@test.com"))
                .thenReturn("jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    // ================= LOGIN FAILURE (WRONG PASSWORD) =================
    @Test
    void shouldFailLoginWhenPasswordIsWrong() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("wrong-password");

        UserDetails userDetails = User.withUsername("user@test.com")
                .password("encoded-password")
                .authorities("ROLE_USER")
                .build();

        when(userDetailsService.loadUserByUsername("user@test.com"))
                .thenReturn(userDetails);

        when(passwordEncoder.matches("wrong-password", "encoded-password"))
                .thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
