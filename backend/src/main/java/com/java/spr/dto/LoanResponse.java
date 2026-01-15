package com.java.spr.dto;

import com.java.spr.model.Financials;
import com.java.spr.model.enums.LoanStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class LoanResponse {

    private String id;
    private String applicantEmail;
    private BigDecimal amount;
    private Integer tenureMonths;
    private double interestRate;

    private BigDecimal emi;
    private BigDecimal totalPayableAmount;
    private BigDecimal totalInterest;

    private String clientName;
    private String loanType;
    private Financials financials;

    private LoanStatus status;
    private LocalDateTime createdAt;

    private List<LoanAction> actions;
}

