package com.java.spr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.spr.dto.LoanRequest;
import com.java.spr.dto.LoanStatusRequest;
import com.java.spr.model.Loan;
import com.java.spr.model.enums.LoanStatus;
import com.java.spr.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LoanControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private LoanService loanService;

    @InjectMocks
    private LoanController loanController;

    private Authentication authentication;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();

        authentication = new UsernamePasswordAuthenticationToken(
                "user@test.com",
                null,
                List.of()
        );

        mockMvc = MockMvcBuilders
                .standaloneSetup(loanController)
                .build();
    }

    // ================= CREATE LOAN =================
    @Test
    void shouldCreateLoan() throws Exception {

        LoanRequest request = new LoanRequest();
        request.setAmount(BigDecimal.valueOf(500000));
        request.setTenureMonths(36);
        request.setInterestRate(11.5);

        Loan loan = Loan.builder()
                .id("loan123")
                .status(LoanStatus.DRAFT)
                .build();

        when(loanService.createLoan(any(), eq("user@test.com")))
                .thenReturn(loan);

        mockMvc.perform(post("/api/loans")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("loan123"))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    // ================= GET LOAN BY ID =================
    @Test
    void shouldGetLoanById() throws Exception {

        Loan loan = Loan.builder()
                .id("loan123")
                .status(LoanStatus.DRAFT)
                .build();

        when(loanService.getLoanByIdForUser(eq("loan123"), any()))
                .thenReturn(loan);

        mockMvc.perform(get("/api/loans/loan123")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("loan123"))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    // ================= UPDATE LOAN =================
    @Test
    void shouldUpdateLoan() throws Exception {

        LoanRequest request = new LoanRequest();
        request.setAmount(BigDecimal.valueOf(600000));
        request.setTenureMonths(48);
        request.setInterestRate(12.0);

        Loan loan = Loan.builder()
                .id("loan123")
                .status(LoanStatus.DRAFT)
                .build();

        when(loanService.updateLoan(eq("loan123"), any(), eq("user@test.com")))
                .thenReturn(loan);

        mockMvc.perform(put("/api/loans/loan123")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("loan123"));
    }

    // ================= CHANGE LOAN STATUS =================
    @Test
    void shouldChangeLoanStatus() throws Exception {

        LoanStatusRequest request =
                new LoanStatusRequest("SUBMITTED", "Submitting loan");

        Loan loan = Loan.builder()
                .id("loan123")
                .status(LoanStatus.SUBMITTED)
                .build();

        when(loanService.changeLoanStatus(
                eq("loan123"),
                any(),
                eq("user@test.com"),
                any()
        )).thenReturn(loan);

        mockMvc.perform(patch("/api/loans/loan123/status")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUBMITTED"));
    }

    // ================= DELETE LOAN =================
    @Test
    void shouldDeleteLoan() throws Exception {

        mockMvc.perform(delete("/api/loans/loan123"))
                .andExpect(status().isOk());
    }
}
