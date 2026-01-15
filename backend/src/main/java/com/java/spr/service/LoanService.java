package com.java.spr.service;

import com.java.spr.dto.LoanRequest;
import com.java.spr.dto.LoanStatusRequest;
import com.java.spr.kafka.producer.LoanEventProducer;
import com.java.spr.kafka.events.LoanEvent;
import com.java.spr.model.Loan;
import com.java.spr.model.LoanAction;
import com.java.spr.model.enums.LoanStatus;
import com.java.spr.repository.LoanRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final LoanEventProducer loanEventProducer;


    // ================= CREATE =================
    public Loan createLoan(@NotNull LoanRequest request, String userEmail) {

        Loan loan = Loan.builder()
                .applicantEmail(userEmail)
                .amount(request.getAmount())
                .tenureMonths(request.getTenureMonths())
                .interestRate(request.getInterestRate())

                .clientName(request.getClientName())
                .loanType(request.getLoanType())
                .financials(request.getFinancials())

                .status(LoanStatus.DRAFT)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();

        calculatePricing(loan);

        Loan savedLoan = loanRepository.save(loan);

        // Publish Kafka event (NEW)
        loanEventProducer.publishLoanStatusChange(
                new LoanEvent(
                        savedLoan.getId(),
                        null,              // no old status (loan just created)
                        LoanStatus.DRAFT.name(),   // new status
                        userEmail
                )
        );

        return savedLoan;
    }



    // ================= PAGINATED READ =================
    public Page<Loan> getLoansPaged(
            Authentication authentication,
            int page,
            int size,
            LoanStatus status
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        String email = authentication.getName();


        if (isAdmin) {
            if (status != null) {
                return loanRepository.findByStatusAndDeletedFalse(status, pageable);
            }
            return loanRepository.findByDeletedFalse(pageable);
        }


        if (status != null) {
            return loanRepository.findByApplicantEmailAndStatusAndDeletedFalse(
                    email, status, pageable
            );
        }

        return loanRepository.findByApplicantEmailAndDeletedFalse(email, pageable);
    }



    // ================= READ BY ID =================
    public Loan getLoanByIdForUser(String id, Authentication authentication) {

        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !loan.getApplicantEmail().equals(authentication.getName())) {
            throw new RuntimeException("You are not allowed to view this loan");
        }

        return loan;
    }

    // ================= UPDATE (DRAFT ONLY) =================
    public Loan updateLoan(String loanId, LoanRequest request, String userEmail) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (loan.getStatus() != LoanStatus.DRAFT) {
            throw new RuntimeException("Only DRAFT loans can be edited");
        }

        if (!loan.getApplicantEmail().equals(userEmail)) {
            throw new RuntimeException("You are not allowed to edit this loan");
        }

        loan.setAmount(request.getAmount());
        loan.setTenureMonths(request.getTenureMonths());
        loan.setInterestRate(request.getInterestRate());

        calculatePricing(loan);
        loan.setUpdatedAt(LocalDateTime.now());

        return loanRepository.save(loan);
    }

    // ================= STATUS CHANGE (CORE LOGIC) =================
    public Loan changeLoanStatus(
            String loanId,
            LoanStatusRequest request,
            String email,
            Collection<? extends GrantedAuthority> authorities
    ) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        LoanStatus currentStatus = loan.getStatus();
        LoanStatus nextStatus = LoanStatus.valueOf(request.getStatus());

        boolean isAdmin = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isUser = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));



        if (isUser) {
            if (currentStatus != LoanStatus.DRAFT || nextStatus != LoanStatus.SUBMITTED) {
                throw new RuntimeException("USER can only submit DRAFT loans");
            }
            if (!loan.getApplicantEmail().equals(email)) {
                throw new RuntimeException("You can submit only your own loan");
            }
            calculatePricing(loan);
        }


        if (isAdmin) {
            if (currentStatus == LoanStatus.SUBMITTED && nextStatus != LoanStatus.UNDER_REVIEW) {
                throw new RuntimeException("ADMIN must move SUBMITTED → UNDER_REVIEW");
            }

            if (currentStatus == LoanStatus.UNDER_REVIEW &&
                    !(nextStatus == LoanStatus.APPROVED || nextStatus == LoanStatus.REJECTED)) {
                throw new RuntimeException("ADMIN must APPROVE or REJECT");
            }
        }
// capture old status BEFORE change
        LoanStatus oldStatus = currentStatus;

// status change
        loan.setStatus(nextStatus);
        loan.setUpdatedAt(LocalDateTime.now());

// audit trail
        loan.getActions().add(
                new LoanAction(
                        email,
                        nextStatus.name(),
                        request.getComments(),
                        LocalDateTime.now()
                )
        );

// save first (transactional safety)
        Loan savedLoan = loanRepository.save(loan);

// publish Kafka event
        loanEventProducer.publishLoanStatusChange(
                new LoanEvent(
                        savedLoan.getId(),
                        oldStatus.name(),
                        nextStatus.name(),
                        email
                )
        );

        return savedLoan;

    }

    // ================= SOFT DELETE =================
    public void deleteLoan(String loanId) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (loan.isDeleted()) {
            throw new RuntimeException("Loan already deleted");
        }

        loan.setDeleted(true);
        loan.setDeletedAt(LocalDateTime.now());
        loan.setUpdatedAt(LocalDateTime.now());

        loanRepository.save(loan);
    }




    // ================= LOAN CALCULATION =================

    private void calculatePricing(Loan loan) {

        BigDecimal principal = loan.getAmount();              // P
        int months = loan.getTenureMonths();                   // N
        double annualRate = loan.getInterestRate();            // %

        // r = annualRate / (12 * 100)
        BigDecimal monthlyRate = BigDecimal.valueOf(annualRate)
                .divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_UP);

        // (1 + r)^n
        BigDecimal onePlusRPowerN =
                monthlyRate.add(BigDecimal.ONE).pow(months);

        // EMI formula:
        // EMI = P * r * (1+r)^n / ((1+r)^n - 1)
        BigDecimal emi = principal
                .multiply(monthlyRate)
                .multiply(onePlusRPowerN)
                .divide(
                        onePlusRPowerN.subtract(BigDecimal.ONE),
                        2,
                        RoundingMode.HALF_UP
                );

        BigDecimal totalPayable =
                emi.multiply(BigDecimal.valueOf(months));

        BigDecimal totalInterest =
                totalPayable.subtract(principal);

        loan.setEmi(emi);
        loan.setTotalPayableAmount(totalPayable);
        loan.setTotalInterest(totalInterest);
    }


}
