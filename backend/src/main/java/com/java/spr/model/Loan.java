package com.java.spr.model;

import com.java.spr.model.enums.LoanStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "loans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    @Id
    private String id;


    private String applicantEmail;
    private BigDecimal amount;
    private Integer tenureMonths;
    private double interestRate;
    private LoanStatus status;

    private boolean deleted;
    private LocalDateTime deletedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    private BigDecimal emi;
    private BigDecimal totalInterest;
    private BigDecimal totalPayableAmount;


    private String clientName;
    private String loanType;

    private Financials financials;

    @Builder.Default
    private List<LoanAction> actions = new ArrayList<>();
}
