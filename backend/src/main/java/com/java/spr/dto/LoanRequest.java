package com.java.spr.dto;

import com.java.spr.model.Financials;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanRequest {

    private BigDecimal amount;

    private int tenureMonths;

    private double interestRate;

    private String clientName;
    private String loanType;
    private Financials financials;

}
