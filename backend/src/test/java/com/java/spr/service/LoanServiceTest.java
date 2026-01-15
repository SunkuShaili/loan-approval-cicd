package com.java.spr.service;

import com.java.spr.dto.LoanRequest;
import com.java.spr.dto.LoanStatusRequest;
import com.java.spr.kafka.producer.LoanEventProducer;
import com.java.spr.model.Loan;
import com.java.spr.model.enums.LoanStatus;
import com.java.spr.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanEventProducer loanEventProducer;

    @InjectMocks
    private LoanService loanService;

    // ===================== CREATE LOAN =====================
    @Test
    void shouldCreateLoanAndCalculatePricing() {

        LoanRequest request = new LoanRequest();
        request.setAmount(new BigDecimal("600000"));
        request.setTenureMonths(48);
        request.setInterestRate(11.5);

        when(loanRepository.save(any(Loan.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        doNothing().when(loanEventProducer)
                .publishLoanStatusChange(any());

        Loan loan = loanService.createLoan(request, "user@test.com");

        assertThat(loan.getApplicantEmail()).isEqualTo("user@test.com");
        assertThat(loan.getStatus()).isEqualTo(LoanStatus.DRAFT);
        assertThat(loan.getEmi()).isNotNull();
        assertThat(loan.getTotalInterest()).isNotNull();
        assertThat(loan.getTotalPayableAmount()).isNotNull();

        verify(loanRepository, times(1)).save(any(Loan.class));
        verify(loanEventProducer, times(1)).publishLoanStatusChange(any());
    }

    // ===================== STATUS CHANGE =====================
    @Test
    void userCanSubmitDraftLoan() {

        Loan loan = Loan.builder()
                .id("loan123")
                .applicantEmail("user@test.com")
                .amount(BigDecimal.valueOf(600000))
                .tenureMonths(48)
                .interestRate(11.5)
                .status(LoanStatus.DRAFT)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .actions(new ArrayList<>())
                .build();

        when(loanRepository.findById("loan123"))
                .thenReturn(Optional.of(loan));

        when(loanRepository.save(any(Loan.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        doNothing().when(loanEventProducer)
                .publishLoanStatusChange(any());

        Loan updatedLoan = loanService.changeLoanStatus(
                "loan123",
                new LoanStatusRequest("SUBMITTED", "Submitting loan"),
                "user@test.com",
                List.of(() -> "ROLE_USER")
        );

        assertThat(updatedLoan.getStatus()).isEqualTo(LoanStatus.SUBMITTED);
        assertThat(updatedLoan.getActions()).hasSize(1);
    }

    @Test
    void adminCanMoveSubmittedToUnderReview() {

        Loan loan = Loan.builder()
                .id("loan123")
                .applicantEmail("user@test.com")
                .status(LoanStatus.SUBMITTED)
                .actions(new ArrayList<>())
                .build();

        when(loanRepository.findById("loan123"))
                .thenReturn(Optional.of(loan));

        when(loanRepository.save(any(Loan.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        doNothing().when(loanEventProducer)
                .publishLoanStatusChange(any());

        Loan updatedLoan = loanService.changeLoanStatus(
                "loan123",
                new LoanStatusRequest("UNDER_REVIEW", "Checking docs"),
                "admin@bank.com",
                List.of(() -> "ROLE_ADMIN")
        );

        assertThat(updatedLoan.getStatus()).isEqualTo(LoanStatus.UNDER_REVIEW);
        assertThat(updatedLoan.getActions()).hasSize(1);
    }

    // ===================== INVALID ACTIONS =====================
    @Test
    void userCannotApproveLoan() {

        Loan loan = Loan.builder()
                .id("loan123")
                .status(LoanStatus.UNDER_REVIEW)
                .actions(new ArrayList<>())
                .build();

        when(loanRepository.findById("loan123"))
                .thenReturn(Optional.of(loan));

        assertThrows(RuntimeException.class, () ->
                loanService.changeLoanStatus(
                        "loan123",
                        new LoanStatusRequest("APPROVED", "Trying to approve"),
                        "user@test.com",
                        List.of(() -> "ROLE_USER")
                )
        );
    }

    @Test
    void adminCannotApproveFromSubmitted() {

        Loan loan = Loan.builder()
                .id("loan123")
                .status(LoanStatus.SUBMITTED)
                .actions(new ArrayList<>())
                .build();

        when(loanRepository.findById("loan123"))
                .thenReturn(Optional.of(loan));

        assertThrows(RuntimeException.class, () ->
                loanService.changeLoanStatus(
                        "loan123",
                        new LoanStatusRequest("APPROVED", "Invalid move"),
                        "admin@bank.com",
                        List.of(() -> "ROLE_ADMIN")
                )
        );
    }
}
