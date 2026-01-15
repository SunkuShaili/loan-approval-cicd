package com.java.spr.controller;

import com.java.spr.dto.LoanRequest;
import com.java.spr.dto.LoanResponse;
import com.java.spr.dto.LoanStatusRequest;
import com.java.spr.mapper.LoanMapper;
import com.java.spr.model.Loan;
import com.java.spr.model.enums.LoanStatus;
import com.java.spr.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    // ================= GET LOANS =================
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Page<LoanResponse> getLoans(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) LoanStatus status
    ) {
        return loanService
                .getLoansPaged(authentication, page, size, status)
                .map(LoanMapper::toDto);
    }

    // ================= GET LOAN BY ID =================
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public LoanResponse getLoanById(
            @PathVariable String id,
            Authentication authentication
    ) {
        return LoanMapper.toDto(
                loanService.getLoanByIdForUser(id, authentication)
        );
    }

    // ================= CREATE LOAN =================
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Loan createLoan(
            @RequestBody LoanRequest loanRequest,
            Authentication authentication
    ) {
        return loanService.createLoan(loanRequest, authentication.getName());
    }

    // ================= UPDATE LOAN (DRAFT ONLY) =================
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public Loan updateLoan(
            @PathVariable String id,
            @RequestBody LoanRequest loanRequest,
            Authentication authentication
    ) {
        return loanService.updateLoan(id, loanRequest, authentication.getName());
    }

    // ================= CHANGE LOAN STATUS =================
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Loan changeLoanStatus(
            @PathVariable String id,
            @RequestBody LoanStatusRequest request,
            Authentication authentication
    ) {
        return loanService.changeLoanStatus(
                id,
                request,
                authentication.getName(),
                authentication.getAuthorities()
        );
    }

    // ================= SOFT DELETE =================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteLoan(@PathVariable String id) {
        loanService.deleteLoan(id);
    }
}
